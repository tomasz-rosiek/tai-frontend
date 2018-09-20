/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.tai.util.factory

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.tai.model.domain.{TaxCodeChangeReason, TaxCodeChangeReasons}
import uk.gov.hmrc.tai.util.{TaiConstants, TaxCodeChangeReasonTypeAdded, TaxCodeChangeReasonTypeRemoved}

object TaxCodeChangeReasonsFactory {

  def create: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq(TaxCodeChangeReasonFactory.createNewEmploymentReason, TaxCodeChangeReasonFactory.createCeasedEmploymentReason))
  }

  def createEmpty: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq())
  }

  def createJson: JsObject = {
    Json.obj(
      "data" -> Json.obj(
        "reasons" -> Json.arr(
          TaxCodeChangeReasonFactory.createNewEmploymentReasonJson,
          TaxCodeChangeReasonFactory.createCeasedEmploymentReasonJson
        )
      )
    )
  }

  def createEmptyJson: JsObject = {
    Json.obj(
      "data" -> Json.obj(
        "reasons" -> Json.arr()
      )
    )
  }
}

object TaxCodeChangeReasonFactory {
  def createNewEmploymentReason: TaxCodeChangeReason = {
    TaxCodeChangeReason(TaxCodeChangeReasonTypeAdded, "EMPLOYMENT", "Some development reasons")
  }

  def createNewEmploymentReasonJson: JsObject = {
    Json.obj(
      "reasonsType" -> "ADDED",
      "id" -> "EMPLOYMENT",
      "reason" -> "Some development reasons"
    )
  }

  def createCeasedEmploymentReason: TaxCodeChangeReason = {
    TaxCodeChangeReason(TaxCodeChangeReasonTypeRemoved, "EMPLOYMENT", "Some development reasons")
  }

  def createCeasedEmploymentReasonJson: JsObject = {
    Json.obj(
      "reasonsType" -> "REMOVED",
      "id" -> "EMPLOYMENT",
      "reason" -> "Some development reasons"
    )
  }
}
