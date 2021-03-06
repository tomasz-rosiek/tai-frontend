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

package views.html.employments

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.tai.forms.employments.UpdateRemoveEmploymentForm
import uk.gov.hmrc.tai.util.FormValuesConstants
import uk.gov.hmrc.tai.util.viewHelpers.TaiViewSpec

class update_remove_employment_decisionSpec extends TaiViewSpec with FormValuesConstants {
  val employmentName = "Employment Name"
  val empId = 1
  val updateRemoveEmploymentForm: Form[Option[String]] = UpdateRemoveEmploymentForm.form.bind(
    Map(UpdateRemoveEmploymentForm.EmploymentDecision -> YesValue))

  override def view: Html = views.html.employments.update_remove_employment_decision(
    updateRemoveForm = updateRemoveEmploymentForm,
    employmentName = employmentName,
    empId = empId)

  "update_remove_employment_decision" must {
    behave like pageWithTitle(messages("tai.employment.decision.title", employmentName))

    behave like pageWithBackLink

    behave like pageWithCombinedHeader(
      preHeaderText = messages("tai.employment.decision.preHeading"),
      mainHeaderText = messages("tai.employment.decision.heading", employmentName))

    behave like pageWithYesNoRadioButton(
      UpdateRemoveEmploymentForm.EmploymentDecision+"-yes",
      UpdateRemoveEmploymentForm.EmploymentDecision+"-no",
      messages("tai.employment.decision.radio1"),
      messages("tai.employment.decision.radio2"))

    behave like pageWithContinueButtonForm(controllers.employments.routes.EndEmploymentController.handleEmploymentUpdateRemove(empId).url)

    behave like pageWithCancelLink(controllers.routes.IncomeSourceSummaryController.onPageLoad(empId))
  }
}