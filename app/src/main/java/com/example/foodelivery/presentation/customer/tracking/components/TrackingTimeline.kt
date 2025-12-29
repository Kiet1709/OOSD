package com.example.foodelivery.presentation.customer.tracking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodelivery.presentation.customer.tracking.contract.TrackingStep
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun TrackingTimeline(currentStep: TrackingStep) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        TrackingStep.values().forEachIndexed { index, step ->
            val isCompleted = currentStep.ordinal >= step.ordinal
            val isCurrent = currentStep == step
            val isLast = index == TrackingStep.values().lastIndex

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                // Cột bên trái: Dấu chấm và đường kẻ
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) PrimaryColor else Color.LightGray)
                            .then(if (isCurrent) Modifier.border(2.dp, PrimaryColor.copy(0.3f), CircleShape) else Modifier)
                    )
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(if (isCompleted && currentStep.ordinal > step.ordinal) PrimaryColor else Color.LightGray)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Cột bên phải: Text
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCompleted) Color.Black else Color.Gray
                    )
                    if (isCurrent) {
                        Text(text = step.description, style = MaterialTheme.typography.bodySmall, color = PrimaryColor)
                    }
                }
            }
        }
    }
}