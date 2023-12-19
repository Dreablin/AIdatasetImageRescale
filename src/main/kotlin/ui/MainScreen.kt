package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun MainScreen(fileList: List<String>) {
    val files = remember { fileList }
    var text by remember { mutableStateOf("Hello, World!") }

    Row(
        verticalAlignment = Alignment.Top
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.25f)
        ) {
            items(files) {
                FileListEntry(it)
            }
        }
    }
}

@Composable
fun FileListEntry(fileName: String) {
    val text = remember { mutableStateOf(fileName) }
    Box(
        modifier = Modifier
            .padding(5.dp)
            .clickable {
//                AllData.shownFile.value = text.value
            }
    ) {
        Text(
            text = text.value
        )
    }
}