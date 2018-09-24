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

package uk.gov.hmrc.tai.service

import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.domain.{Generator, Nino}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.tai.connectors.{TaxAccountConnector, TaxCodeChangeConnector}
import uk.gov.hmrc.tai.connectors.responses.{TaiSuccessResponseWithPayload, TaiTaxAccountFailureResponse}
import uk.gov.hmrc.tai.model.domain.income.OtherBasisOperation
import uk.gov.hmrc.tai.model.domain.{TaxCodeChange, TaxCodeComparison, TaxCodeRecord}
import uk.gov.hmrc.tai.util.factory.TaxCodeChangeReasonsFactory
import uk.gov.hmrc.time.TaxYearResolver

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Random

class TaxCodeChangeServiceSpec extends PlaySpec with MockitoSugar{

  "taxCodeChange" must {
    "return the tax code change given a valid nino" in {
      val taxCodeChangeServiceMock = createTaxCodeChangeServiceMock
      val nino = generateNino

      val taxCodeChange = TaxCodeChange(Seq(taxCodeRecord1), Seq(taxCodeRecord2))

      when(taxCodeChangeServiceMock.taxCodeChangeConnector.taxCodeChange(any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(taxCodeChange)))

      val result = taxCodeChangeServiceMock.taxCodeChange(nino)
      Await.result(result, 5.seconds) mustBe taxCodeChange
    }
  }

  "has tax code changed" must {
    "return true if there has been a tax code change" in {
      val taxCodeChangeServiceMock = createTaxCodeChangeServiceMock
      val nino = generateNino
      when(taxCodeChangeServiceMock.taxCodeChangeConnector.hasTaxCodeChanged(any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(true)))
      val result = taxCodeChangeServiceMock.hasTaxCodeChanged(nino)
      Await.result(result, 5.seconds) mustBe true
    }
  }

  "tax code change reasons" must {
    "return tax code change reason" when {
      "a valid object is returned" in {
        val taxCodeChangeServiceMock = createTaxCodeChangeServiceMock
        val nino = generateNino

        val taxCodeChangeReasons = TaxCodeChangeReasonsFactory.create

        when(taxCodeChangeServiceMock.taxCodeChangeConnector.taxCodeChangeReasons(any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(taxCodeChangeReasons)))

        val result = taxCodeChangeServiceMock.taxCodeChangeReasons(nino)
        Await.result(result, 5.seconds) mustBe taxCodeChangeReasons
      }
    }
    "return a Runtime exception" when {
      "no valid object is returned" in {
        val taxCodeChangeServiceMock = createTaxCodeChangeServiceMock
        val nino = generateNino
        val failure = TaiTaxAccountFailureResponse("")

        when(taxCodeChangeServiceMock.taxCodeChangeConnector.taxCodeChangeReasons(any())(any())).thenReturn(Future.successful(failure))

        val exception = the[RuntimeException] thrownBy Await.result(taxCodeChangeServiceMock.taxCodeChangeReasons(nino), 5.seconds)
        exception.getMessage mustBe "Could not fetch tax code change reasons"
      }
    }
  }

  "TaxCodeChangeComparison" must {
    "returns a TaxCodeComparison with valid change, reasons and scottish tax rate bands" when {
      "all API calls are successful" in {
        val taxCodeChangeServiceMock = createTaxCodeChangeServiceMock
        val nino = generateNino

        val taxCodeChange = TaxCodeChange(Seq(taxCodeRecord1), Seq(taxCodeRecord2))
        val taxCodeChangeReasons = TaxCodeChangeReasonsFactory.create
        val scottishTaxRateBands = Map[String, BigDecimal]()

        when(taxCodeChangeServiceMock.taxCodeChangeConnector.taxCodeChange(any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(taxCodeChange)))
        when(taxCodeChangeServiceMock.taxCodeChangeConnector.taxCodeChangeReasons(any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(taxCodeChangeReasons)))
        when(taxCodeChangeServiceMock.taxAccountService.scottishBandRates(any(), any(), any())(any())).thenReturn(Future.successful(scottishTaxRateBands))

        val result = taxCodeChangeServiceMock.taxCodeComparison(nino)
        Await.result(result, 5.seconds) mustBe TaxCodeComparison(taxCodeChange, taxCodeChangeReasons, scottishTaxRateBands)
      }
    }
    "return a Runtime exception" when {
      "one API call fails" in {
        val taxCodeChangeServiceMock = createTaxCodeChangeServiceMock
        val nino = generateNino

        val failure = TaiTaxAccountFailureResponse("")
        val taxCodeChangeReasons = TaxCodeChangeReasonsFactory.create
        val scottishTaxRateBands = Map[String, BigDecimal]()

        when(taxCodeChangeServiceMock.taxCodeChangeConnector.taxCodeChange(any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(failure)))
        when(taxCodeChangeServiceMock.taxCodeChangeConnector.taxCodeChangeReasons(any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(taxCodeChangeReasons)))
        when(taxCodeChangeServiceMock.taxAccountService.scottishBandRates(any(), any(), any())(any())).thenReturn(Future.successful(scottishTaxRateBands))

        val exception = the[RuntimeException] thrownBy Await.result(taxCodeChangeServiceMock.taxCodeComparison(nino), 5.seconds)
        exception.getMessage mustBe "Could not fetch tax code change"
      }
    }
  }

  val startDate = TaxYearResolver.startOfCurrentTaxYear
  val taxCodeRecord1 = TaxCodeRecord("code", startDate, startDate.plusDays(1), OtherBasisOperation,"Employer 1", false, Some("1234"), true)
  val taxCodeRecord2 = taxCodeRecord1.copy(startDate = startDate.plusDays(2), endDate = TaxYearResolver.endOfCurrentTaxYear)

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  private def generateNino: Nino = new Generator(new Random).nextNino
  private def createTaxCodeChangeServiceMock = new TaxCodeChangeServiceMock

  private class TaxCodeChangeServiceMock extends TaxCodeChangeService {
    override val taxCodeChangeConnector: TaxCodeChangeConnector = mock[TaxCodeChangeConnector]
    override val taxAccountService: TaxAccountService = mock[TaxAccountService]
  }
}
