package com.example.compose.jetsurvey.survey

import android.content.Intent
import android.net.Uri
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.R.id.sign_in_fragment
import com.example.compose.jetsurvey.survey.SurveyActionType.*
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker

class SurveyFragment : Fragment() {

    private val viewModel: SurveyViewModel by viewModels {
        SurveyViewModelFactory(PhotoUriManager(requireContext().applicationContext))
    }

    private val takePicture = registerForActivityResult(TakePicture()) { photoSaved ->
        if (photoSaved) viewModel.onImageSaved()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        // In order for savedState to work, the same ID needs to be used for all instances.
        id = sign_in_fragment

        layoutParams = ViewGroup.LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT
        )
        setContent {
            JetsurveyTheme {
                viewModel.uiState.observeAsState().value?.let { surveyState ->
                    when (surveyState) {
                        is SurveyState.Questions -> SurveyQuestionsScreen(
                            questions = surveyState,
                            shouldAskPermissions = viewModel.askForPermissions,
                            onAction = { id, action -> handleSurveyAction(id, action) },
                            onDoNotAskForPermissions = { viewModel.doNotAskForPermissions() },
                            onDonePressed = { viewModel.computeResult(surveyState) },
                            onBackPressed = {
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            },
                            openSettings = {
                                activity?.startActivity(
                                    Intent(
                                        ACTION_APPLICATION_DETAILS_SETTINGS,
                                        fromParts("package", context.packageName, null)
                                    )
                                )
                            }
                        )
                        is SurveyState.Result -> SurveyResultScreen(
                            result = surveyState,
                            onDonePressed = {
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun handleSurveyAction(questionId: Int, actionType: SurveyActionType) {
        when (actionType) {
            PICK_DATE -> showDatePicker(questionId)
            TAKE_PHOTO -> takeAPhoto()
            SELECT_CONTACT -> selectContact(questionId)
        }
    }

    private fun showDatePicker(questionId: Int) {
        val date = viewModel.getCurrentDate(questionId = questionId)
        val picker = datePicker()
            .setSelection(date)
            .build()
        activity?.let {
            picker.show(it.supportFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                viewModel.onDatePicked(questionId, picker.selection)
            }
        }
    }

    private fun takeAPhoto() {
        takePicture.launch(viewModel.getUriToSaveImage())
    }

    @Suppress("UNUSED_PARAMETER")
    private fun selectContact(questionId: Int) {
        // TODO: unsupported for now
    }
}