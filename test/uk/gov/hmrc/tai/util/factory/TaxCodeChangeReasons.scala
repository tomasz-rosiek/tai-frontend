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

import play.api.libs.json.{JsArray, JsObject, Json}
import uk.gov.hmrc.tai.model.domain._

object TaxCodeChangeReasonsFactory {

  def create: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq(TaxCodeChangeReasonFactory.createNewEmploymentReason, TaxCodeChangeReasonFactory.createCeasedEmploymentReason))
  }

  def createWithSingleAllowanceReason: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq(TaxCodeChangeReasonFactory.createAdjustedAllowanceReason))
  }

  def createWithTwoReasons: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq(TaxCodeChangeReasonFactory.createNewEmploymentReason, TaxCodeChangeReasonFactory.createCeasedEmploymentReason))
  }

  def createWithThreeReasons: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq(
      TaxCodeChangeReasonFactory.createNewEmploymentReason,
      TaxCodeChangeReasonFactory.createCeasedEmploymentReason,
      TaxCodeChangeReasonFactory.createAdjustedAllowanceReason
    ))
  }

  def createWithFourReasons: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq(
      TaxCodeChangeReasonFactory.createNewEmploymentReason,
      TaxCodeChangeReasonFactory.createCeasedEmploymentReason,
      TaxCodeChangeReasonFactory.createAdjustedAllowanceReason,
      TaxCodeChangeReasonFactory.createAdjustedExpenseReason
    ))
  }

  def createEmpty: TaxCodeChangeReasons = {
    TaxCodeChangeReasons(Seq())
  }

  def createJson: JsObject = {
    val reasons = Json.arr(
      TaxCodeChangeReasonFactory.createNewEmploymentReasonJson,
      TaxCodeChangeReasonFactory.createCeasedEmploymentReasonJson
    )

    employmentReasonsJson(reasons)
  }

  def createEmptyJson: JsObject = {
    employmentReasonsJson(Json.arr())
  }

  private def employmentReasonsJson(reasons: JsArray): JsObject = {
    Json.obj (
      "data" -> Json.obj(
        "reasons" -> reasons
      )
    )
  }
}

object TaxCodeChangeReasonFactory {
  def createNewEmploymentReason: TaxCodeChangeReason = {
    TaxCodeChangeReason(TaxCodeChangeReasonTypeAdded, "EMPLOYMENT", "Description")
  }

  def createNewEmploymentReasonJson: JsObject = {
    employmentReasonJSON("ADDED","EMPLOYMENT","Description")
  }

  def createCeasedEmploymentReason: TaxCodeChangeReason = {
    TaxCodeChangeReason(TaxCodeChangeReasonTypeRemoved, "EMPLOYMENT", "Description")
  }

  def createAdjustedAllowanceReason: TaxCodeChangeReason = {
    TaxCodeChangeReason(TaxCodeChangeReasonTypeAdjusted, "ALLOWANCE", "Description")
  }

  def createAdjustedExpenseReason: TaxCodeChangeReason = {
    TaxCodeChangeReason(TaxCodeChangeReasonTypeAdjusted, "EXPENSE", "Description")
  }

  def createCeasedEmploymentReasonJson: JsObject = {
    employmentReasonJSON("REMOVED","EMPLOYMENT","Description")
  }

  private def employmentReasonJSON(reason: String, id: String, description: String): JsObject = {
    Json.obj(
      "reason" -> reason,
      "id" -> id,
      "description" -> description
    )
  }
}
