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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsNull, JsResultException, Json}
import uk.gov.hmrc.domain.{Generator, Nino}
import uk.gov.hmrc.tai.util.factory.TaxCodeChangeReasonsFactory

import scala.util.Random

class TaxCodeChangeReasonsSpec extends PlaySpec {
  "TaxCodeChangeReasons" when {
    "parsing JSON" should {
      "return a valid TaxCodeChangeReasons object when given valid Json" in {
        val expectedModel = TaxCodeChangeReasonsFactory.create
        val json = TaxCodeChangeReasonsFactory.createJson

        (json \ "data").as[TaxCodeChangeReasons] mustEqual expectedModel
      }

      "return a valid TaxCodeChangeReasons object when given an empty array" in {
        val expectedModel = TaxCodeChangeReasonsFactory.createEmpty
        val json = TaxCodeChangeReasonsFactory.createEmptyJson

        (json \ "data").as[TaxCodeChangeReasons] mustEqual expectedModel
      }


      "throw a JsError given an empty data" in {
        an[JsResultException] should be thrownBy emptyReasons.as[TaxCodeChangeReasons]
      }
    }
  }

  val emptyReasons = Json.obj("data" -> JsNull)

  private def generateNino: Nino = new Generator(new Random).nextNino
}
