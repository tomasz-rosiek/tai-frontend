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

package views.html.incomes

import org.scalatest.mock.MockitoSugar
import play.api.mvc.Call
import play.twirl.api.Html
import uk.gov.hmrc.tai.util.viewHelpers.TaiViewSpec

class EstimatedPaySpec extends TaiViewSpec with MockitoSugar {

  val id = 1
  val employerName = "Employer"

  "Estimated pay view without bonusOverTime" should {
    behave like pageWithBackLink
    behave like pageWithCancelLink(Call("GET", controllers.routes.IncomeSourceSummaryController.onPageLoad(id).url))
    behave like pageWithCombinedHeader(
      messages("tai.estimatedPay.preHeading", employerName),
      messages("tai.estimatedPay.title"))
  }
  "Estimated pay view with bonusOverTime" should {
    val testView: Html = views.html.incomes.estimatedPay(None,None,id,true,None,None,employerName,false)
    doc(testView) must haveBackLink
  }

  override def view: Html = views.html.incomes.estimatedPay(None,None,id,false,None,None,employerName,false)
}
