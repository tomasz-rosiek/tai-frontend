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

package controllers

import builders.{AuthBuilder, RequestBuilder}
import mocks.MockTemplateRenderer
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers._
import uk.gov.hmrc.domain.{Generator, Nino}
import uk.gov.hmrc.play.frontend.auth.connectors.{AuthConnector, DelegationConnector}
import uk.gov.hmrc.play.partials.FormPartialRetriever
import uk.gov.hmrc.renderer.TemplateRenderer
import uk.gov.hmrc.tai.connectors.responses.{TaiResponse, TaiSuccessResponseWithPayload, TaiTaxAccountFailureResponse}
import uk.gov.hmrc.tai.model.domain.TaxAccountSummary
import uk.gov.hmrc.tai.model.domain.income.{NonTaxCodeIncome, TaxCodeIncome}
import uk.gov.hmrc.tai.model.domain.tax.{IncomeCategory, TotalTax}
import uk.gov.hmrc.tai.service.{CodingComponentService, PersonService, TaxAccountService}

import scala.concurrent.Future
import scala.util.Random

class DetailedIncomeTaxEstimateControllerSpec extends PlaySpec with MockitoSugar with FakeTaiPlayApplication {

  "Detailed Income Tax Estimate Controller" must {
    "return OK when responses are " when {
      "there are bands present" in {
        val sut = createSUT
        val result = sut.taxExplanationPage()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe OK
      }
    }

    "return Internal server error" when {
      "fetch total tax details fails" in {
        val sut = createSUT
        when(sut.taxAccountService.totalTax(any(), any())(any())).thenReturn(Future.successful(TaiTaxAccountFailureResponse("testFailure")))
        val result = sut.taxExplanationPage()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "fetch tax code incomes fails" in {
        val sut = createSUT
        when(sut.taxAccountService.taxCodeIncomes(any(), any())(any())).thenReturn(Future.successful(TaiTaxAccountFailureResponse("testFailure")))
        val result = sut.taxExplanationPage()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "fetch tax account summary fails" in {
        val sut = createSUT
        when(sut.taxAccountService.taxAccountSummary(any(), any())(any())).thenReturn(Future.successful(TaiTaxAccountFailureResponse("testFailure")))
        val result = sut.taxExplanationPage()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "fetch of non-tax code incomes fails" in {
        val sut = createSUT
        when(sut.taxAccountService.nonTaxCodeIncomes(any(), any())(any())).thenReturn(Future.successful(TaiTaxAccountFailureResponse("testFailure")))
        val result = sut.taxExplanationPage()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "fetch of tax free amount components" in {
        val sut = createSUT
        when(sut.codingComponentService.taxFreeAmountComponents(any(), any())(any())).thenReturn(Future.failed(new Error))
        val result = sut.taxExplanationPage()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }


  val nino: Nino = new Generator(new Random).nextNino
  private def createSUT = new SUT

  class SUT extends DetailedIncomeTaxEstimateController {
    override val personService: PersonService = mock[PersonService]
    override val taxAccountService: TaxAccountService = mock[TaxAccountService]
    override val codingComponentService: CodingComponentService = mock[CodingComponentService]

    override protected val authConnector: AuthConnector = mock[AuthConnector]
    override protected val delegationConnector: DelegationConnector = mock[DelegationConnector]
    override implicit val templateRenderer: TemplateRenderer = MockTemplateRenderer
    override implicit val partialRetriever: FormPartialRetriever = mock[FormPartialRetriever]

    when(personService.personDetails(any())(any())).thenReturn(Future.successful(fakePerson(nino)))
    when(authConnector.currentAuthority(any(), any())).thenReturn(AuthBuilder.createFakeAuthData)
    when(taxAccountService.totalTax(any(), any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(TotalTax(0, Seq.empty, None, None, None))))
    when(taxAccountService.taxCodeIncomes(any(), any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(Seq.empty[TaxCodeIncome])))
    when(taxAccountService.taxAccountSummary(any(), any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(TaxAccountSummary(0,0,0,0,0))))
    when(taxAccountService.nonTaxCodeIncomes(any(), any())(any())).thenReturn(Future.successful(TaiSuccessResponseWithPayload(NonTaxCodeIncome(None, Seq.empty))))
    when(codingComponentService.taxFreeAmountComponents(any(), any())(any())).thenReturn(Future.successful(Seq.empty))
  }
}

