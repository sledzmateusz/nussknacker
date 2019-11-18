package pl.touk.nussknacker.engine.api.dict.static

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import pl.touk.nussknacker.engine.api.dict.{DictDefinition, DictRegistry}
import pl.touk.nussknacker.engine.api.dict.DictRegistry.{DictEntryWithKeyNotExists, DictEntryWithLabelNotExists, DictNotDeclared}

trait StaticDictRegistry extends DictRegistry {

  protected def declarations: Map[String, DictDefinition]

  override def keyByLabel(dictId: String, label: String): Validated[DictRegistry.DictLookupError, String] = {
    declarations.get(dictId).map(Valid(_)).getOrElse(Invalid(DictNotDeclared(dictId))).andThen {
      case static: StaticDictDefinition =>
        static.keyByLabel.get(label).map(key => Valid(key))
          .getOrElse(Invalid(DictEntryWithLabelNotExists(dictId, label, Some(static.keyByLabel.values.toList))))
      case definition =>
        handleNotStaticUiKeyBeLabel(definition, label)
    }
  }

  override def labelByKey(dictId: String, key: String): Validated[DictRegistry.DictLookupError, Option[String]] = {
    declarations.get(dictId).map(Valid(_)).getOrElse(Invalid(DictNotDeclared(dictId))).andThen {
      case static: StaticDictDefinition =>
        static.labelByKey.get(key).map(key => Valid(Some(key)))
          .getOrElse(Invalid(DictEntryWithKeyNotExists(dictId, key, Some(static.labelByKey.values.toList))))
      case definition =>
        handleNotStaticLabelByKey(definition, key)
    }
  }

  protected def handleNotStaticUiKeyBeLabel(definition: DictDefinition, label: String): Validated[DictRegistry.DictEntryWithLabelNotExists, String]

  protected def handleNotStaticLabelByKey(definition: DictDefinition, key: String): Validated[DictEntryWithKeyNotExists, Option[String]]

}
