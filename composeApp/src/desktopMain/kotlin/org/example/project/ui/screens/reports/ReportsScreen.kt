package org.example.project.ui.screens.reports

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import cafe.adriel.voyager.core.screen.Screen
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.IndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import org.example.project.domain.models.toReadableString
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.random.Random

data class ReportsScreen(val activeMonth: LocalDate) : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        val vm = koinViewModel<ReportsScreenViewModel>()
        val uiState by vm.lineChartState.collectAsStateWithLifecycle()

        val pieChartState by vm.pieChartState.collectAsStateWithLifecycle()

        LaunchedEffect(activeMonth) {
            vm.getDataForMonth(activeMonth)
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MonthlyExpensesCard(pieChartState)
            IncomeAndExpenses(uiState)
        }
    }
}

@Composable
private fun MonthlyExpensesCard(
    state: ReportsScreenViewModel.PieChartState,
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Monthly expenses",
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
                                modifier = Modifier.widthIn(min = 200.dp)
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
                                    state.groupSpendingPercentage[group.group].toString() + "%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.widthIn(min = 200.dp)
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
                                text = "Uncategorized",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            (100 - state.groupSpendingPercentage.values.sum()).toString() + "%",
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
                            onPieClick = {},
                            selectedPaddingDegree = 4f,
                            style = Pie.Style.Stroke()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Total spending:",
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
private fun IncomeAndExpenses(
    state: ReportsScreenViewModel.LineChartState
) {

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            if (!state.isLoading) {
                val maxValue by remember {
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
                        labels = state.lineChartLabels
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
                )
            }
        }
    }
}

fun generateRandomColor(): Color {
    val hue = Random.nextInt(0, 360) // Random hue between 0 and 360
    val saturation = Random.nextInt(35, 65) // Low saturation for pastel colors
    val lightness = Random.nextInt(65, 80) // High lightness for bright colors

    val color = Color.hsl(
        hue = hue.toFloat(),
        saturation = saturation.toFloat() / 100,
        lightness = lightness.toFloat() / 100,
    )
    return color
}

fun closestHigherMultipleOf1000(number: Double?): Double {
    return if (number != null) {
        ((number / 1000).toInt() * 1000 + 1000.0)
    } else
        1000.0
}
