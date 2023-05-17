package com.rmaprojects.teachers.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceCard(
    studentName: String,
    studentNis: String,
    studentYearId: Int,
    onDropdownChange: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }

    var pickedStatusValue by remember {
        mutableStateOf("Hadir")
    }

    val statusList = listOf(
        "Hadir", "Izin", "Alpha"
    )

    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .weight(.5F)
                    .size(32.dp),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .weight(1.5F)
            ) {
                Text(
                    text = studentName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = studentNis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .weight(1F),
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(),
                    value = pickedStatusValue,
                    onValueChange = {},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    readOnly = true,
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }) {
                    statusList.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(text = status) },
                            onClick = {
                                pickedStatusValue = status
                                onDropdownChange(
                                    status, studentYearId
                                )
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun StudentAttendanceCardPreview() {
    StudentAttendanceCard(
        "Mamang Sumamang",
        "123141412",
        123,
        { _, _ -> }
    )
}