package com.example.foodelivery.presentation.customer.orderdetail.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class OrderDetailIntent : ViewIntent {
    // For now, this can be empty
    // We might add things like 'Re-order' or 'Cancel' later
}
