package org.example.project.ui.screens.reports

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.monthly_expenses
import budgetmanager.composeapp.generated.resources.total_spending
import budgetmanager.composeapp.generated.resources.uncategorized
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.VerticalScrollBar
import org.example.project.utils.closestHigherMultipleOf1000
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.math.absoluteValue


@OptIn(KoinExperimentalAPI::class)
@Composable
fun ReportsScreen(activeMonth: LocalDate) {
    val vm = koinViewModel<ReportsScreenViewModel>()
    val lineChartState by vm.lineChartState.collectAsStateWithLifecycle()

    val pieChartState by vm.pieChartState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState(0)
    LaunchedEffect(activeMonth) {
        vm.getDataForMonth(activeMonth)
    }
    Row {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.verticalScroll(scrollState).weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PieChart(pieChartState, modifier = Modifier.wrapContentHeight(Alignment.Top))
            LineChart(lineChartState, modifier = Modifier.heightIn(min = 200.dp, max = 300.dp))
        }
        VerticalScrollBar(scrollState, Modifier.fillMaxHeight())
    }

}

@Composable
private fun PieChart(
    state: ReportsScreenViewModel.PieChartState,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.monthly_expenses),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Row {
                Column {
                    state.groupsWithCategories.filter { !it.group.isIncomeGroup }
                        .forEach { group ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.widthIn(min = 230.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(15.dp)
                                            .clip(CircleShape)
                                            .background(color = group.group.color)
                                            .padding(top = 4.dp)
                                    )
                                    Text(
                                        text = group.group.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    state.groupSpendingPercentage[group.group].toPercentageString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.widthIn(min = 230.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(15.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                                    .padding(top = 4.dp)
                            )
                            Text(
                                text = stringResource(Res.string.uncategorized),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        val uncategorized = 100 - state.groupSpendingPercentage.values.sum()
                        Text(
                            uncategorized.toPercentageString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                }
                val animatedPies = state.groupPies.map { pie ->
                    val animatedValue by animateFloatAsState(targetValue = pie.data.toFloat())
                    pie.copy(data = animatedValue.toDouble())
                }
                if (!state.isLoading)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        PieChart(
                            data = animatedPies,
                            modifier = Modifier.size(300.dp),
                            selectedScale = 1.2f,
                            selectedPaddingDegree = 4f,
                            style = Pie.Style.Stroke()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(Res.string.total_spending) + ":",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Text(
                                state.totalSpending
                                    .toReadableString(true),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun LineChart(
    state: ReportsScreenViewModel.LineChartState,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            if (!state.isLoading) {
                val maxValue by remember(state.incomeExpenseLines) {
                    mutableStateOf(
                        closestHigherMultipleOf1000(state.incomeExpenseLines.flatMap { it.values }
                            .maxOrNull()
                        )
                    )
                }
                LineChart(
                    data = state.incomeExpenseLines,
                    curvedEdges = false,
                    labelProperties = LabelProperties(
                        enabled = true,
                        labels = state.lineChartLabels,
                    ),
                    maxValue = maxValue,
                    gridProperties = GridProperties(
                        xAxisProperties = GridProperties.AxisProperties(
                            enabled = true,
                            lineCount = (maxValue / 1000).toInt() + 1
                        ),
                        yAxisProperties = GridProperties.AxisProperties(
                            enabled = true,
                            lineCount = state.lineChartLabels.size
                        )
                    ),
                    indicatorProperties = IndicatorProperties(
                        enabled = true,
                        contentBuilder = {
                            val formatter = NumberFormat.getNumberInstance(Locale("pl", "PL"))
                            formatter.format(it) + " zł"
                        }
                    ),
                    popupProperties = PopupProperties(
                        enabled = true,
                        contentBuilder = {
                            val formatter = NumberFormat.getNumberInstance(Locale("pl", "PL"))
                            formatter.format(it) + " zł"
                        }
                    )
                )
            }
        }
    }
}

fun Double?.toPercentageString(): String {
    return this?.let {
        String.format("%.1f", this.absoluteValue) + " %"
    } ?: "0,0 %"

}