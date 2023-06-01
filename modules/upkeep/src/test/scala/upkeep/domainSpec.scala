package upkeep

import cats.implicits.*
import org.scalatest.*

import flatspec.*
import matchers.*

class DomainSpec extends AnyFlatSpec with should.Matchers:
  import domain.*
  import DomainSpec.*

  "Replacing existing dependency" should "replace version" in {
    val initialTxtContent   = requirementsTxtTemplate("foo", "==", "1.2.3")
    val expectedTxtContent  = requirementsTxtTemplate("foo", "==", "2.0.0")
    val initialTomlContent  = pyProjectTomlTemplate("foo", "^", "1.2.3")
    val expectedTomlContent = pyProjectTomlTemplate("foo", "^", "2.0.0")

    val actualTxtContent = replaceDependency(
      FileType.Txt,
      initialTxtContent,
      "foo",
      "1.2.3",
      "2.0.0"
    )
    val actualTomlContent = replaceDependency(
      FileType.Toml,
      initialTomlContent,
      "foo",
      "1.2.3",
      "2.0.0"
    )

    actualTxtContent shouldBe expectedTxtContent
    actualTomlContent shouldBe expectedTomlContent
  }

  "Replacing non-existing dependency" should "keep the file content the same" in {
    val initialTxtContent  = requirementsTxtTemplate("foo", "==", "1.2.3")
    val initialTomlContent = pyProjectTomlTemplate("foo", "^", "1.2.3")

    val actualTxtContent = replaceDependency(
      FileType.Txt,
      initialTxtContent,
      "bar",
      "1.2.3",
      "2.0.0"
    )
    val actualTomlContent = replaceDependency(
      FileType.Toml,
      initialTomlContent,
      "bar",
      "1.2.3",
      "2.0.0"
    )

    actualTxtContent shouldBe initialTxtContent
    actualTomlContent shouldBe initialTomlContent
  }

  "Replacing dependency with a different current version" should "keep the file content the same" in {
    val initialTxtContent  = requirementsTxtTemplate("foo", "==", "1.2.3")
    val initialTomlContent = pyProjectTomlTemplate("foo", "^", "1.2.3")

    val actualTxtContent = replaceDependency(
      FileType.Txt,
      initialTxtContent,
      "foo",
      "1.4.5",
      "2.0.0"
    )
    val actualTomlContent = replaceDependency(
      FileType.Toml,
      initialTomlContent,
      "foo",
      "1.4.5",
      "2.0.0"
    )

    actualTxtContent shouldBe initialTxtContent
    actualTomlContent shouldBe initialTomlContent
  }

  "Replacing dependency with similar-but-not-exact-matching name" should "keep the file content the same" in {
    val initialTxtContent  = requirementsTxtTemplate("foo", "==", "1.2.3")
    val initialTomlContent = pyProjectTomlTemplate("foo", "^", "1.2.3")

    val actualTxtContent1 = replaceDependency(
      FileType.Txt,
      initialTxtContent,
      "foobar",
      "1.2.3",
      "2.0.0"
    )
    val actualTxtContent2 = replaceDependency(
      FileType.Txt,
      initialTxtContent,
      "barfoo",
      "1.2.3",
      "2.0.0"
    )
    val actualTomlContent1 = replaceDependency(
      FileType.Toml,
      initialTomlContent,
      "foobar",
      "1.2.3",
      "2.0.0"
    )
    val actualTomlContent2 = replaceDependency(
      FileType.Toml,
      initialTomlContent,
      "barfoo",
      "1.2.3",
      "2.0.0"
    )

    actualTxtContent1 shouldBe initialTxtContent
    actualTxtContent2 shouldBe initialTxtContent
    actualTomlContent1 shouldBe initialTomlContent
    actualTomlContent2 shouldBe initialTomlContent
  }

object DomainSpec:
  def requirementsTxtTemplate(
      name: String,
      symbol: "==" | ">=" | "",
      version: String
  ): String = s"""
  |a==1.2.3
  |$name$symbol$version
  |b>=1.2.3
  """.stripMargin

  def pyProjectTomlTemplate(
      name: String,
      symbol: "^" | "",
      version: String
  ): String = s"""
  |a = "1.2.3"
  |$name = "$version"
  |b = "1.2.3"
  """.stripMargin
