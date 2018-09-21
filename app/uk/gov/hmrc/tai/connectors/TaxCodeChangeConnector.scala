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

package uk.gov.hmrc.tai.connectors

import play.api.Logger
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.tai.connectors.EmploymentsConnector.baseUrl
import uk.gov.hmrc.tai.connectors.responses.{TaiResponse, TaiSuccessResponseWithPayload, TaiTaxAccountFailureResponse}
import uk.gov.hmrc.tai.model.domain.{TaxCodeChange, TaxCodeChangeReasons}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.parsing.json.JSON

trait TaxCodeChangeConnector {

  val serviceUrl: String

  def httpHandler: HttpHandler

  def baseTaxCodeChangeUrl(nino: String): String = s"$serviceUrl/tai/$nino/tax-account/tax-code-change"

  def taxCodeChangeUrl(nino: String): String = baseTaxCodeChangeUrl(nino)
  def hasTaxCodeChangedUrl(nino: String): String = s"${baseTaxCodeChangeUrl(nino)}/exists"
  def taxCodeChangeReasonsUrl(nino: String): String = s"${baseTaxCodeChangeUrl(nino)}/reasons"

  def taxCodeChange(nino: Nino)(implicit hc: HeaderCarrier): Future[TaiResponse] = {
    httpHandler.getFromApi(taxCodeChangeUrl(nino.nino)) map (
      json =>
        TaiSuccessResponseWithPayload((json \ "data").as[TaxCodeChange])
      ) recover {
      case e: Exception =>
        Logger.warn(s"Couldn't retrieve tax code change for $nino with exception:${e.getMessage}")
        TaiTaxAccountFailureResponse(e.getMessage)
    }
  }

  def hasTaxCodeChanged(nino: Nino)(implicit hc: HeaderCarrier): Future[TaiResponse] = {
    httpHandler.getFromApi(hasTaxCodeChangedUrl(nino.nino)) map (
        json => TaiSuccessResponseWithPayload(json.as[Boolean])
      ) recover {
      case e: Exception =>
        Logger.warn(s"Couldn't retrieve tax code changed for $nino with exception:${e.getMessage}")
        TaiTaxAccountFailureResponse(e.getMessage)
    }
  }

  def taxCodeChangeReasons(nino: Nino)(implicit hc: HeaderCarrier): Future[TaiResponse] = {
    httpHandler.getFromApi(taxCodeChangeReasonsUrl(nino.nino)) map (
        json => TaiSuccessResponseWithPayload((json \ "data").as[TaxCodeChangeReasons])
      ) recover {
      case e: Exception =>
        Logger.warn(s"Couldn't retrieve tax code change reasons for $nino with exception:${e.getMessage}")
        TaiTaxAccountFailureResponse(e.getMessage)
    }
  }
}

object TaxCodeChangeConnector extends TaxCodeChangeConnector{
  override val serviceUrl = baseUrl("tai")
  override def httpHandler: HttpHandler = HttpHandler
}