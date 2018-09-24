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

package uk.gov.hmrc.tai.model.domain

import play.api.libs.json.{Format, JsString, JsSuccess, JsValue}

sealed trait TaxCodeChangeReasonType {
  def identifier: String
  override def toString: String = this.identifier
}

case object TaxCodeChangeReasonTypeAdded extends TaxCodeChangeReasonType {
  override def identifier: String = "ADDED"
}
case object TaxCodeChangeReasonTypeRemoved extends TaxCodeChangeReasonType {
  override def identifier: String = "REMOVED"
}
case object TaxCodeChangeReasonTypeAdjusted extends TaxCodeChangeReasonType {
  override def identifier: String = "ADJUSTED"
}

object TaxCodeChangeReasonType extends TaxCodeChangeReasonType {
  override def identifier: String = ""

  implicit val format = new Format[TaxCodeChangeReasonType] {
    override def reads(json: JsValue): JsSuccess[TaxCodeChangeReasonType] = json.as[String] match {
      case "ADDED" => JsSuccess(TaxCodeChangeReasonTypeAdded)
      case "REMOVED" => JsSuccess(TaxCodeChangeReasonTypeRemoved)
      case "ADJUSTED" => JsSuccess(TaxCodeChangeReasonTypeAdjusted)
      case _ => throw new IllegalArgumentException("Invalid TaxCodeChangeReasonsType")
    }

    override def writes(taxCodeChangeReasonsType: TaxCodeChangeReasonType) = JsString(taxCodeChangeReasonsType.toString)
  }
}
