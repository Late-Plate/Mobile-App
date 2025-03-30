package com.example.late_plate.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.view_model.InventoryPopUpState


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
                ){
                    CustomCard {
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
                                    text = "${btnText} inventory item",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {onDismiss()}
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .weight(0.01f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.weight(0.1f)
                            ) {
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = "Item Name",
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(R.color.grey_white)
                                )
                                Box(
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = "*",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            CustomTextFieldPopupSearch(
                                input = inputName,
                                selected = selectFromNER.value,
                                placeholderText = "Enter item's name",
                                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                                onEdit = onEdit,
                                readOnly = status == InventoryPopUpState.UPDATE
                            )
                            
                            Row(
                                modifier = Modifier.weight(0.1f)
                            ) {
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = "Quantity",
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(R.color.grey_white)
                                )
                                Box(
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = "*",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.weight(0.17f).wrapContentHeight(),
                            ) {
                                Box(modifier = Modifier.weight(2f)) { // ✅ Wrap in Box to allow proper weight distribution
                                    CustomTextFieldPopup(
                                        input = inputQuantity,
                                        placeholderText = "Enter Quantity",
                                        modifier = Modifier.wrapContentWidth().wrapContentHeight(), // ✅ Ensure it takes full width inside Box
                                        keyboardType = KeyboardType.Number
                                    )
                                }
                                Box(modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()) {
                                    ComboBox(inputType, modifier = Modifier.wrapContentWidth().wrapContentHeight())
                                }
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .weight(0.01f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier
                                    .weight(0.2f)
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Button(
                                    onClick = onDismiss,
                                    shape = RoundedCornerShape(34),
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(R.color.cancel_btn_color),  // ✅ Background color
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        text = "Cancel",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        if(inputName.value.isNotEmpty() && inputQuantity.value.isNotEmpty())
                                            onConfirm(inputName.value, inputQuantity.value.toFloat(), inputType.value)

                                    },
                                    shape = RoundedCornerShape(34),
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        text = btnText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }

    }


}

@Preview
@Composable
fun PreviewCustomInventoryPopup(){
//    CustomInventoryPopup(Modifier, true, {}, {}, 0.35f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    selectedOption: MutableState<String>,
    modifier: Modifier
) {
    val options = listOf("kg", "gm", "unit")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedOption.value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(4.dp),

            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Dropdown Icon"
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption.value = option
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PrevCombo(){
//    ComboBox(modifier = Modifier)
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
        onValueChange = { input.value = it },
        placeholder = { Text(text = placeholderText, fontSize = 14.sp)},
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        modifier = modifier // ✅ Use the passed modifier
            .fillMaxWidth()
            .padding(4.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType  // ✅ Correct usage
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
    readOnly: Boolean = false
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

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Set max height for scrolling
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { expanded = false } // Dismiss on click outside
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


