package com.dev.jcctech.agroguardian.ui.screen.agribrain

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dev.jcctech.agroguardian.data.db.entity.CropEntity
import com.dev.jcctech.agroguardian.data.db.entity.RotationPlanEntity
import com.dev.jcctech.agroguardian.data.db.entity.SoilConditionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectionScreen(
    crops: List<CropEntity> = emptyList(),
    soil: SoilConditionEntity? = null,
    plans: List<RotationPlanEntity> = emptyList(),
    onGeneratePlan: () -> Unit = {}
) {
    val latestPlan = plans.maxByOrNull { it.generatedAt }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showProjection by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState: DatePickerState = rememberDatePickerState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .padding(8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        Text("Calendario de cultivos", style = MaterialTheme.typography.titleMedium)
                    }
                    Button(onClick = { showDatePicker = true }) {
                        Text("Seleccionar día")
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .padding(8.dp)
        ) {
            Card(modifier = Modifier.fillMaxSize()) {
                if (selectedDateMillis == null) {
                    GeneralProjectionView(
                        crops = crops,
                        soil = soil,
                        plan = latestPlan,
                        onGeneratePlan = onGeneratePlan,
                        onShowProjection = { showProjection = true }
                    )
                } else {
                    DayDetailView(
                        selectedDateMillis = selectedDateMillis,
                        crops = crops,
                        plan = latestPlan,
                        onShowProjection = { showProjection = true }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showProjection) {
        ModalBottomSheet(
            onDismissRequest = { showProjection = false },
            sheetState = sheetState
        ) {
            ProjectionDetailSheet(crops = crops, plan = latestPlan)
        }
    }
}

@Composable
fun GeneralProjectionView(
    crops: List<CropEntity>,
    soil: SoilConditionEntity?,
    plan: RotationPlanEntity?,
    onGeneratePlan: () -> Unit,
    onShowProjection: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Resumen general", style = MaterialTheme.typography.titleMedium) }

        soil?.let { s ->
            item {
                ExpansionTile(title = "Condiciones de suelo", icon = Icons.Default.WaterDrop) {
                    Text("Tipo: ${s.type}")
                    s.ph?.let { Text("pH: $it") }
                    s.organicMatter?.let { Text("Materia orgánica: $it %") }
                    s.drainage?.let { Text("Drenaje: $it") }
                }
            }
        }

        items(crops.sortedByDescending { it.startDate }.take(3)) { crop ->
            ExpansionTile(title = crop.name, icon = Icons.Default.Spa) {
                Text("Familia: ${crop.type}")
                Text("Inicio: ${crop.startDate}")
                crop.endDate?.let { Text("Fin: $it") }
            }
        }

        plan?.let { p ->
            item {
                ExpansionTile(title = "Plan actual", icon = Icons.Default.Schedule) {
                    Text("Generado: ${p.generatedAt}")
                    Spacer(Modifier.height(8.dp))
                    p.recommendedSequence.forEachIndexed { index, c ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            Text("${index + 1}. $c")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Razonamiento")
                    Text(p.rationale, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onShowProjection, modifier = Modifier.fillMaxWidth()) {
                        Text("Ver proyección detallada")
                    }
                }
            }
        }

        item {
            Button(onClick = onGeneratePlan, modifier = Modifier.fillMaxWidth()) {
                Text("Generar plan rotativo")
            }
        }
    }
}

@Composable
fun DayDetailView(
    selectedDateMillis: Long?,
    crops: List<CropEntity>,
    plan: RotationPlanEntity?,
    onShowProjection: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Info, contentDescription = null)
            Text("Detalles del día ${selectedDateMillis ?: "-"}", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()
        plan?.let { p ->
            Text("Recomendación del día")
            if (p.recommendedSequence.isNotEmpty()) {
                val current = crops.maxByOrNull { it.startDate }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Actual")
                        current?.let {
                            Text(it.name)
                            Text(it.type, style = MaterialTheme.typography.bodySmall)
                        } ?: Text("Sin cultivo activo")
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Destino")
                        Text(p.recommendedSequence.first())
                    }
                }
            } else {
                Text("No hay secuencia recomendada")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onShowProjection, modifier = Modifier.fillMaxWidth()) {
                Text("Ver conversión detallada")
            }
        } ?: Text("No hay plan disponible")
    }
}

@Composable
fun ProjectionDetailSheet(crops: List<CropEntity>, plan: RotationPlanEntity?) {
    val current = crops.maxByOrNull { it.startDate }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Conversión de cultivo", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider()
        if (current != null && plan != null && plan.recommendedSequence.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Actual")
                    Text(current.name)
                    Text(current.type, style = MaterialTheme.typography.bodySmall)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Siguiente")
                    Text(plan.recommendedSequence.first())
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Secuencia completa")
            plan.recommendedSequence.forEachIndexed { index, c ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${index + 1}.")
                    Text(c)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Razonamiento")
            Text(plan.rationale, style = MaterialTheme.typography.bodySmall)
        } else {
            Text("No hay datos suficientes para mostrar la conversión")
        }
    }
}

@Composable
fun ExpansionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null)
            Text(title, modifier = Modifier.weight(1f))
            IconButton(onClick = { expanded = !expanded }) {
                Icon(if (expanded) Icons.Default.Info else Icons.Default.CalendarMonth, contentDescription = null)
            }
        }
        if (expanded) {
            Column(modifier = Modifier.padding(start = 56.dp, end = 16.dp, bottom = 12.dp), content = content)
        }
        HorizontalDivider()
    }
}