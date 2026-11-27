package com.backendbadi.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import com.backendbadi.orders.entity.OrderStatus;
import com.backendbadi.orders.entity.TiffinOrder;
import com.backendbadi.orders.messaging.OrderEventPublisher;
import com.backendbadi.orders.messaging.OrderPlacedEvent;
import com.backendbadi.orders.model.OrderRequest;
import com.backendbadi.orders.model.OrderResponse;
import com.backendbadi.orders.repository.TiffinOrderRepository;
import com.backendbadi.orders.service.OrderService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import org.springframework.test.util.ReflectionTestUtils;

/** Plain unit tests: no Spring context, no broker. */
class OrderServiceTest {

    private final TiffinOrderRepository repository = mock(TiffinOrderRepository.class);

    private final OrderEventPublisher publisher = mock(OrderEventPublisher.class);

    private final OrderService service = new OrderService(repository, publisher);

    @Test
    void savesFirstThenTellsTheKitchen() {
        given(repository.save(any(TiffinOrder.class))).willAnswer(invocation -> {
            TiffinOrder order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 7L);   // what the database would do
            return order;
        });

        OrderResponse response = service.placeOrder(new OrderRequest(" Ravi ", "Masala Dosa", 2));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.status()).isEqualTo(OrderStatus.PLACED);

        ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        InOrder order = inOrder(repository, publisher);
        order.verify(repository).save(any(TiffinOrder.class));
        order.verify(publisher).publish(event.capture());
        assertThat(event.getValue()).isEqualTo(new OrderPlacedEvent(7L, "Ravi", "Masala Dosa", 2));
    }

    @Test
    void startCookingChangesTheStatus() {
        TiffinOrder order = new TiffinOrder("Sita", "Idli", 3);
        given(repository.findById(1L)).willReturn(Optional.of(order));

        service.startCooking(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COOKING);
    }
}
