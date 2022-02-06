package com.example.compose.jetsurvey.survey

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.RadioButtonDefaults.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.text.style.TextAlign.Companion.End
import androidx.compose.ui.text.style.TextAlign.Companion.Start
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.R.drawable.ic_selfie_dark
import com.example.compose.jetsurvey.R.drawable.ic_selfie_light
import com.example.compose.jetsurvey.R.string.open_settings
import com.example.compose.jetsurvey.R.string.permissions_denied
import com.example.compose.jetsurvey.survey.Answer.MultipleChoice
import com.example.compose.jetsurvey.survey.Answer.PermissionsDenied
import com.example.compose.jetsurvey.survey.PossibleAnswer.SingleChoice
import com.example.compose.jetsurvey.survey.SurveyActionResult.Photo
import com.example.compose.jetsurvey.survey.SurveyActionType.*
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.getDefault

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Question(
    question: Question,
    answer: Answer<*>?,
    shouldAskPermissions: Boolean,
    onAnswer: (Answer<*>) -> Unit,
    onAction: (Int, SurveyActionType) -> Unit,
    onDoNotAskForPermissions: () -> Unit,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (question.permissionsRequired.isEmpty())
        QuestionContent(question, answer, onAnswer, onAction, modifier)
    else {
        val permissionsContentModifier = modifier.padding(horizontal = 20.dp)

        val multiplePermissionsState =
            rememberMultiplePermissionsState(question.permissionsRequired)

        when {
            // If all permissions are granted, then show the question
            multiplePermissionsState.allPermissionsGranted -> {
                QuestionContent(question, answer, onAnswer, onAction, modifier)
            }
            // If user denied some permissions but a rationale should be shown or the user
            // is going to be presented with the permission for the first time. Let's explain
            // why we need the permission
            multiplePermissionsState.shouldShowRationale ||
                    !multiplePermissionsState.permissionRequested -> {
                if (!shouldAskPermissions)
                    PermissionsDenied(
                        question.questionText,
                        openSettings,
                        permissionsContentModifier
                    )
                else PermissionsRationale(
                    question,
                    multiplePermissionsState,
                    onDoNotAskForPermissions,
                    permissionsContentModifier
                )
            }
            // If the criteria above hasn't been met, the user denied some permission.
            else -> {
                PermissionsDenied(
                    question.questionText,
                    openSettings,
                    permissionsContentModifier
                )
                // Trigger side-effect to not ask for permissions
                LaunchedEffect(true) {
                    onDoNotAskForPermissions()
                }
            }
        }

        // If permissions are denied, inform the caller that can move to the next question
        if (!shouldAskPermissions) LaunchedEffect(true) {
            onAnswer(PermissionsDenied)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionsRationale(
    question: Question,
    multiplePermissionsState: MultiplePermissionsState,
    onDoNotAskForPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(32.dp))
        QuestionTitle(question.questionText)
        Spacer(modifier = Modifier.height(32.dp))
        val rationaleId =
            question.permissionsRationaleText ?: R.string.permissions_rationale
        Text(stringResource(id = rationaleId))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        ) {
            Text(stringResource(R.string.request_permissions))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onDoNotAskForPermissions) {
            Text(stringResource(R.string.do_not_ask_permissions))
        }
    }
}

@Composable
private fun PermissionsDenied(
    @StringRes title: Int,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier) {
    Spacer(modifier = Modifier.height(32.dp))
    QuestionTitle(title)
    Spacer(modifier = Modifier.height(32.dp))
    Text(stringResource(permissions_denied))
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedButton(onClick = openSettings) {
        Text(stringResource(open_settings))
    }
}

@Composable
private fun QuestionContent(
    question: Question,
    answer: Answer<*>?,
    onAnswer: (Answer<*>) -> Unit,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) = LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
) {
    item {
        Spacer(modifier = Modifier.height(32.dp))
        QuestionTitle(question.questionText)
        Spacer(modifier = Modifier.height(24.dp))
        if (question.description != null) {
            CompositionLocalProvider(LocalContentAlpha provides medium) {
                Text(
                    text = stringResource(id = question.description),
                    style = typography.caption,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(bottom = 18.dp, start = 8.dp, end = 8.dp)
                )
            }
        }
        when (question.answer) {
            is SingleChoice -> SingleChoiceQuestion(
                possibleAnswer = question.answer,
                answer = answer as Answer.SingleChoice?,
                onAnswerSelected = { answer -> onAnswer(Answer.SingleChoice(answer)) },
                modifier = Modifier.fillParentMaxWidth()
            )
            is PossibleAnswer.SingleChoiceIcon -> SingleChoiceIconQuestion(
                possibleAnswer = question.answer,
                answer = answer as Answer.SingleChoice?,
                onAnswerSelected = { answer -> onAnswer(Answer.SingleChoice(answer)) },
                modifier = Modifier.fillMaxWidth()
            )
            is PossibleAnswer.MultipleChoice -> MultipleChoiceQuestion(
                possibleAnswer = question.answer,
                answer = answer as MultipleChoice?,
                onAnswerSelected = { newAnswer, selected ->
                    // create the answer if it doesn't exist or
                    // update it based on the user's selection
                    if (answer == null) {
                        onAnswer(MultipleChoice(setOf(newAnswer)))
                    } else {
                        onAnswer(answer.withAnswerSelected(newAnswer, selected))
                    }
                },
                modifier = Modifier.fillParentMaxWidth()
            )
            is PossibleAnswer.MultipleChoiceIcon -> MultipleChoiceIconQuestion(
                possibleAnswer = question.answer,
                answer = answer as MultipleChoice?,
                onAnswerSelected = { newAnswer, selected ->
                    // create the answer if it doesn't exist or
                    // update it based on the user's selection
                    if (answer == null) {
                        onAnswer(MultipleChoice(setOf(newAnswer)))
                    } else {
                        onAnswer(answer.withAnswerSelected(newAnswer, selected))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            is PossibleAnswer.Action -> ActionQuestion(
                questionId = question.id,
                possibleAnswer = question.answer,
                answer = answer as Answer.Action?,
                onAction = onAction,
                modifier = Modifier.fillParentMaxWidth()
            )
            is PossibleAnswer.Slider -> SliderQuestion(
                possibleAnswer = question.answer,
                answer = answer as Answer.Slider?,
                onAnswerSelected = { onAnswer(Answer.Slider(it)) },
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}

@Composable
private fun QuestionTitle(@StringRes title: Int) {
    val backgroundColor = if (colors.isLight) {
        colors.onSurface.copy(alpha = 0.04f)
    } else {
        colors.onSurface.copy(alpha = 0.06f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = shapes.small
            )
    ) {
        Text(
            text = stringResource(id = title),
            style = typography.subtitle1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        )
    }
}

@Composable
private fun SingleChoiceQuestion(
    possibleAnswer: SingleChoice,
    answer: Answer.SingleChoice?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringRes.associateBy { stringResource(id = it) }

    val radioOptions = options.keys.toList()

    val selected = if (answer != null) stringResource(id = answer.answer) else null

    val (selectedOption, onOptionSelected) = remember(answer) { mutableStateOf(selected) }

    Column(modifier = modifier) {
        radioOptions.forEach { text ->
            val onClickHandle = {
                onOptionSelected(text)
                options[text]?.let { onAnswerSelected(it) }
                Unit
            }
            val optionSelected = text == selectedOption

            val answerBorderColor = if (optionSelected) {
                colors.primary.copy(alpha = 0.5f)
            } else {
                colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (optionSelected) {
                colors.primary.copy(alpha = 0.12f)
            } else {
                colors.background
            }
            Surface(
                shape = shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = optionSelected,
                            onClick = onClickHandle
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = SpaceBetween
                ) {
                    Text(text = text)

                    RadioButton(
                        selected = optionSelected,
                        onClick = onClickHandle,
                        colors = colors(
                            selectedColor = colors.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleChoiceIconQuestion(
    possibleAnswer: PossibleAnswer.SingleChoiceIcon,
    answer: Answer.SingleChoice?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringIconRes.associateBy { stringResource(id = it.second) }

    val radioOptions = options.keys.toList()

    val selected = if (answer != null) stringResource(id = answer.answer) else null

    val (selectedOption, onOptionSelected) = remember(answer) { mutableStateOf(selected) }

    Column(modifier = modifier) {
        radioOptions.forEach { text ->
            val onClickHandle = {
                onOptionSelected(text)
                options[text]?.let { onAnswerSelected(it.second) }
                Unit
            }
            val optionSelected = text == selectedOption
            val answerBorderColor = if (optionSelected) {
                colors.primary.copy(alpha = 0.5f)
            } else {
                colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (optionSelected) {
                colors.primary.copy(alpha = 0.12f)
            } else {
                colors.background
            }
            Surface(
                shape = shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = optionSelected,
                            onClick = onClickHandle
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = SpaceBetween
                ) {
                    options[text]?.let {
                        Image(
                            painter = painterResource(
                                id = it.first
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .width(56.dp)
                                .height(56.dp)
                                .clip(shapes.medium)
                        )
                    }
                    Text(
                        text = text
                    )

                    RadioButton(
                        selected = optionSelected,
                        onClick = onClickHandle,
                        colors = colors(
                            selectedColor = colors.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceQuestion(
    possibleAnswer: PossibleAnswer.MultipleChoice,
    answer: MultipleChoice?,
    onAnswerSelected: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringRes.associateBy { stringResource(id = it) }
    Column(modifier = modifier) {
        for (option in options) {
            var checkedState by remember(answer) {
                val selectedOption = answer?.answersStringRes?.contains(option.value)
                mutableStateOf(selectedOption ?: false)
            }
            val answerBorderColor = if (checkedState) {
                colors.primary.copy(alpha = 0.5f)
            } else {
                colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checkedState) {
                colors.primary.copy(alpha = 0.12f)
            } else {
                colors.background
            }
            Surface(
                shape = shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(answerBackgroundColor)
                        .clickable(
                            onClick = {
                                checkedState = !checkedState
                                onAnswerSelected(option.value, checkedState)
                            }
                        )
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = SpaceBetween
                ) {
                    Text(text = option.key)

                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { selected ->
                            checkedState = selected
                            onAnswerSelected(option.value, selected)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.primary
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceIconQuestion(
    possibleAnswer: PossibleAnswer.MultipleChoiceIcon,
    answer: MultipleChoice?,
    onAnswerSelected: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringIconRes.associateBy { stringResource(id = it.second) }
    Column(modifier = modifier) {
        for (option in options) {
            var checkedState by remember(answer) {
                val selectedOption = answer?.answersStringRes?.contains(option.value.second)
                mutableStateOf(selectedOption ?: false)
            }
            val answerBorderColor = if (checkedState) {
                colors.primary.copy(alpha = 0.5f)
            } else {
                colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checkedState) {
                colors.primary.copy(alpha = 0.12f)
            } else {
                colors.background
            }
            Surface(
                shape = shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                checkedState = !checkedState
                                onAnswerSelected(option.value.second, checkedState)
                            }
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = option.value.first),
                        contentDescription = null,
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp)
                            .clip(shapes.medium)
                    )
                    Text(text = option.key)

                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { selected ->
                            checkedState = selected
                            onAnswerSelected(option.value.second, selected)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.primary
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionQuestion(
    questionId: Int,
    possibleAnswer: PossibleAnswer.Action,
    answer: Answer.Action?,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    when (possibleAnswer.actionType) {
        PICK_DATE -> {
            DateQuestion(
                questionId = questionId,
                answer = answer,
                onAction = onAction,
                modifier = modifier
            )
        }
        TAKE_PHOTO -> {
            PhotoQuestion(
                questionId = questionId,
                answer = answer,
                onAction = onAction,
                modifier = modifier
            )
        }
        SELECT_CONTACT -> TODO()
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PhotoQuestion(
    questionId: Int,
    answer: Answer.Action?,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val resource = if (answer != null) {
        Filled.SwapHoriz
    } else {
        Filled.AddAPhoto
    }
    OutlinedButton(
        onClick = { onAction(questionId, TAKE_PHOTO) },
        modifier = modifier,
        contentPadding = PaddingValues()
    ) {
        Column {
            if (answer != null && answer.result is Photo) {
                Image(
                    painter = rememberImagePainter(
                        data = answer.result.uri,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(96.dp)
                        .aspectRatio(4 / 3f)
                )
            } else {
                PhotoDefaultImage(modifier = Modifier.padding(horizontal = 86.dp, vertical = 74.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomCenter)
                    .padding(vertical = 26.dp),
                verticalAlignment = CenterVertically
            ) {
                Icon(imageVector = resource, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        id = if (answer != null) {
                            R.string.retake_photo
                        } else {
                            R.string.add_photo
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun DateQuestion(
    questionId: Int,
    answer: Answer.Action?,
    onAction: (Int, SurveyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val date = if (answer != null && answer.result is SurveyActionResult.Date) {
        answer.result.date
    } else {
        SimpleDateFormat(simpleDateFormatPattern, getDefault()).format(Date())
    }
    Button(
        onClick = { onAction(questionId, PICK_DATE) },
        colors = buttonColors(
            backgroundColor = colors.onPrimary,
            contentColor = colors.onSecondary
        ),
        shape = shapes.small,
        modifier = modifier
            .padding(vertical = 20.dp)
            .height(54.dp),
        elevation = elevation(0.dp),
        border = BorderStroke(1.dp, colors.onSurface.copy(alpha = 0.12f))

    ) {
        Text(
            text = date,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Icon(
            imageVector = Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        )
    }
}

@Composable
private fun PhotoDefaultImage(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = colors.isLight
) {
    val assetId = if (lightTheme) ic_selfie_light else ic_selfie_dark
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
private fun SliderQuestion(
    possibleAnswer: PossibleAnswer.Slider,
    answer: Answer.Slider?,
    onAnswerSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember {
        mutableStateOf(answer?.answerValue ?: possibleAnswer.defaultValue)
    }
    Row(modifier = modifier) {

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onAnswerSelected(it)
            },
            valueRange = possibleAnswer.range,
            steps = possibleAnswer.steps,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
    }
    Row {
        Text(
            text = stringResource(id = possibleAnswer.startText),
            style = typography.caption,
            textAlign = Start,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Text(
            text = stringResource(id = possibleAnswer.neutralText),
            style = typography.caption,
            textAlign = Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Text(
            text = stringResource(id = possibleAnswer.endText),
            style = typography.caption,
            textAlign = End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
    }
}

@Preview
@Composable
fun QuestionPreview() {
    val question = Question(
        id = 2,
        questionText = R.string.pick_superhero,
        answer = SingleChoice(
            optionsStringRes = listOf(
                R.string.spark,
                R.string.lenz,
                R.string.bugchaos,
                R.string.frag
            )
        ),
        description = R.string.select_one
    )
    JetsurveyTheme {
        Question(
            question = question,
            shouldAskPermissions = true,
            answer = null,
            onAnswer = {},
            onAction = { _, _ -> },
            onDoNotAskForPermissions = {},
            openSettings = {}
        )
    }
}