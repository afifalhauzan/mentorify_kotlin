package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cobasupabase.data.dto.ScheduleCreateDto
import com.example.cobasupabase.ui.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJadwalScreen(
    navController: NavHostController,
    id: Long? = null,
    viewModel: ScheduleViewModel = viewModel()
) {
    var teacherName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var timeRange by remember { mutableStateOf("") }

    var expandedDay by remember { mutableStateOf(false) }
    var expandedTime by remember { mutableStateOf(false) }

    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val times = listOf(
        "08:00 - 09:00",
        "09:00 - 10:00",
        "10:00 - 11:00",
        "11:00 - 12:00",
        "13:00 - 14:00",
        "14:00 - 15:00",
        "15:00 - 16:00",
        "16:00 - 17:00"
    )

    LaunchedEffect(id) {
        if (id != null) {
            val schedule = viewModel.getScheduleById(id)
            schedule?.let {
                day = it.day
                timeRange = it.timeRange
                teacherName = it.teacherName
                subject = it.subject
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (id == null) "Tambah Jadwal Les" else "Edit Jadwal Les",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B9BD5)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nama Guru",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = teacherName,
                onValueChange = { teacherName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Masukkan nama guru") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Text(
                text = "Mata Pelajaran",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Masukkan mata pelajaran") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Text(
                text = "Hari",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            ExposedDropdownMenuBox(
                expanded = expandedDay,
                onExpandedChange = { expandedDay = !expandedDay }
            ) {
                OutlinedTextField(
                    value = day,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    placeholder = { Text("Pilih hari") },
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, "Dropdown")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedDay,
                    onDismissRequest = { expandedDay = false }
                ) {
                    days.forEach { dayOption ->
                        DropdownMenuItem(
                            text = { Text(dayOption) },
                            onClick = {
                                day = dayOption
                                expandedDay = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "Jam",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            ExposedDropdownMenuBox(
                expanded = expandedTime,
                onExpandedChange = { expandedTime = !expandedTime }
            ) {
                OutlinedTextField(
                    value = timeRange,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    placeholder = { Text("Pilih jam") },
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, "Dropdown")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedTime,
                    onDismissRequest = { expandedTime = false }
                ) {
                    times.forEach { timeOption ->
                        DropdownMenuItem(
                            text = { Text(timeOption) },
                            onClick = {
                                timeRange = timeOption
                                expandedTime = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (day.isBlank() || timeRange.isBlank()) return@Button

                    val statusValue = if (teacherName.isNotEmpty()) {
                        if (subject.isNotEmpty()) {
                            "$teacherName|$subject"
                        } else {
                            teacherName
                        }
                    } else {
                        subject.ifEmpty { "Available" }
                    }

                    val dto = ScheduleCreateDto(
                        userId = null,
                        teacherId = null,
                        day = day,
                        timeRange = timeRange,
                        status = statusValue
                    )

                    if (id == null) {
                        viewModel.addSchedule(dto)
                    } else {
                        viewModel.updateSchedule(id, dto)
                    }

                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C3E7C)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (id == null) "Tambah" else "Edit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}