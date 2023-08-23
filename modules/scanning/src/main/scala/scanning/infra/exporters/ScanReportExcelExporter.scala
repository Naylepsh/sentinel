package scanning.infra.exporters

import cats.*
import cats.effect.kernel.Sync
import cats.implicits.*
import core.domain.Time
import core.domain.dependency.DependencyReport
import core.domain.project.{ ScanReport, ScanResult }
import core.domain.semver.*
import core.domain.severity.*
import org.joda.time.DateTime
import scanning.domain.Exporter
import spoiwo.model.*
import spoiwo.model.enums.CellFill
import spoiwo.natures.xlsx.Model2XlsxConversions.*

object ScanReportExcelExporter:
  def make[F[_]: Sync: Time](path: String): Exporter[F, ScanReport] = new:
    def exportData(data: List[ScanReport]): F[Unit] =
      Time[F].currentDateTime
        .map(now =>
          Workbook().withSheets(legendSheet :: data.map(toSheet(now)))
        )
        .flatMap(_.saveAsXlsx(path).pure)

    private def toSheet(now: DateTime)(scan: ScanReport): Sheet =
      val rows = scan.dependenciesReports.flatMap(group =>
        val groupName =
          Row(style = headerStyle).withCellValues("Source:", group.groupName)
        val tableDescription = Row(style = headerStyle).withCellValues(columns)
        (groupName
          :: tableDescription
          :: group.items.sortBy(_.name.toLowerCase).map: report =>
            Row(style = chooseStyle(now, report)).withCellValues(
              report.name,
              report.currentVersion.getOrElse(""),
              report.latestVersion,
              report.latestReleaseDate.fold("")(_.toString),
              report.vulnerabilities.length,
              report.vulnerabilities.mkString(",\n"),
              report.notes.getOrElse("")
            )) :+ Row() // Add pseudo "margin-bottom"
      )
      Sheet(name = scan.projectName)
        .withRows(rows)
        .withColumns(columns.map(_ => Column(autoSized = true)))

    private val legendSheet = Sheet(name = "Legend")
      .withRows(
        Row(style = headerStyle).withCellValues("Severity color:") :: List(
          ("Unknown", Severity.Unknown),
          ("None", Severity.None),
          ("Low", Severity.Low),
          ("Medium", Severity.Medium),
          ("High", Severity.High)
        ).map((label, severity) =>
          Row().withCells(Cell(label, style = matchSeverityToStyle(severity)))
        )
      )
      .withColumns(Column(autoSized = true))

    private val columns = List(
      "Name",
      "Current Version",
      "Latest Version",
      "Latest Release Date",
      "Vulnerability Count",
      "Vulnerabilities",
      "Notes"
    )

    private val headerStyle = CellStyle(font = Font(bold = true))

    private def chooseStyle(now: DateTime, report: DependencyReport) =
      matchSeverityToStyle(determineSeverity(now, report))

    private def matchSeverityToStyle(severity: Severity): CellStyle =
      severity match
        case Severity.Unknown => CellStyle()

        case Severity.None =>
          CellStyle(
            fillForegroundColor = Color(47, 158, 156),
            fillPattern = CellFill.Solid
          )

        case Severity.Low =>
          CellStyle(
            fillForegroundColor = Color(173, 216, 230),
            fillPattern = CellFill.Solid
          )

        case Severity.Medium =>
          CellStyle(
            fillForegroundColor = Color(240, 230, 140),
            fillPattern = CellFill.Solid
          )

        case Severity.High =>
          CellStyle(
            fillForegroundColor = Color(255, 99, 71),
            fillPattern = CellFill.Solid
          )
