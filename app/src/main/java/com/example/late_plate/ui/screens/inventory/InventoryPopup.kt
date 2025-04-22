package com.example.late_plate.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.ExpandableSelectionCard
import com.example.late_plate.viewModel.InventoryPopUpState


@Composable
fun CustomInventoryPopup(
    modifier: Modifier,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, Float, String) -> Unit,
    onEdit: (String) -> List<String>,
    height: Float = 0.5f,
    name: String = "",
    quantity: Float = 0f,
    type: String = "kg",
    status: InventoryPopUpState,
    selectNER: MutableState<Boolean>,
    dialogType: String
){
    val inputName = remember { mutableStateOf(name) }
    val inputQuantity = remember { mutableStateOf(quantity.toString()) }
    val inputType = remember { mutableStateOf(type) }
    var selectFromNER = remember { mutableStateOf(selectNER) }
    val btnText = if(status == InventoryPopUpState.ADD) "Add" else "Update"

    if (showDialog){
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(height)
                        .fillMaxWidth()
                ) {
                    CustomCard(contentPadding = 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(0.11f)
                                    .padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "${btnText} ${dialogType} item",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.weight(1f)
                                )

                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(16.dp))
                            CustomTextFieldPopupSearch(
                                input = inputName,
                                selected = selectFromNER.value,
                                placeholderText = "Enter item's name",
                                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                                onEdit = onEdit,
                                readOnly = status == InventoryPopUpState.UPDATE,
                                isGrocery = if(dialogType == "Inventory") false else true
                            )
                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CustomTextFieldPopup(
                                    input = inputQuantity,
                                    placeholderText = "Quantity",
                                    modifier = Modifier.weight(1.1f),
                                    keyboardType = KeyboardType.Number
                                )
                                val options = listOf("kg", "gm", "unit")
                                ExpandableSelectionCard(
                                    selectedOption = inputType,
                                    options = options,
                                    onOptionSelected = { inputType = it },
                                    label = "measure",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier
                                    .padding( 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "cancel",
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(16.dp))
                                        .clickable(onClick =  onDismiss)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                CustomButton(
                                    onClick = {  if (inputName.value.isNotEmpty() && inputQuantity.value.isNotEmpty())
                                        (if(inputQuantity.value.toFloatOrNull()!=null) inputQuantity.value.toFloatOrNull() else 0f)?.let {
                                            onConfirm(
                                                inputName.value,
                                                it,
                                                inputType
                                            )
                                        }
                                    },
                                    content = { Text(btnText) })
                            }
                        }
                    }

                }
            }
        }



@Composable
fun CustomTextFieldPopup(
    input: MutableState<String>,
    placeholderText: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = input.value,
        onValueChange = {
             if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                 input.value = it
             }
            },

        placeholder = { Text(text = placeholderText, fontSize = 14.sp) },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType
        ),
    )
}

@Composable
fun CustomTextFieldPopupSearch(
    input: MutableState<String>,
    selected: MutableState<Boolean>,
    placeholderText: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onEdit: (String) -> List<String>,
    readOnly: Boolean = false,
    isGrocery: Boolean = false
) {
    var filteredIngredients by remember { mutableStateOf(emptyList<String>()) }
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = input.value,
            onValueChange = { newValue ->
                selected.value = false
                input.value = newValue
                filteredIngredients = onEdit(newValue)
                expanded = filteredIngredients.isNotEmpty()
            },
            placeholder = { Text(text = placeholderText, fontSize = 14.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            modifier = modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            keyboardActions = KeyboardActions(
                onDone = { expanded = false }
            ),
            singleLine = true,
            readOnly = readOnly

        )

        if (expanded && !isGrocery) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .heightIn(max = 96.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { expanded = false }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredIngredients) { ingredient ->
                        DropdownMenuItem(
                            text = { Text(ingredient) },
                            onClick = {
                                input.value = ingredient
                                selected.value = true
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


