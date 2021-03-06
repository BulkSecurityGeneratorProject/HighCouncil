package highcouncil.web.rest;

import com.codahale.metrics.annotation.Timed;
import highcouncil.domain.Orders;

import highcouncil.repository.OrdersRepository;
import highcouncil.web.rest.errors.BadRequestAlertException;
import highcouncil.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Orders.
 */
@RestController
@RequestMapping("/api")
public class OrdersResource {

    private final Logger log = LoggerFactory.getLogger(OrdersResource.class);

    private static final String ENTITY_NAME = "orders";

    private final OrdersRepository ordersRepository;

    public OrdersResource(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    /**
     * POST  /orders : Create a new orders.
     *
     * @param orders the orders to create
     * @return the ResponseEntity with status 201 (Created) and with body the new orders, or with status 400 (Bad Request) if the orders has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/orders")
    @Timed
    public ResponseEntity<Orders> createOrders(@Valid @RequestBody Orders orders) throws URISyntaxException {
        log.debug("REST request to save Orders : {}", orders);
        if (orders.getId() != null) {
            throw new BadRequestAlertException("A new orders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (orders.getGame() == null || orders.getGame().getId() == null 
        		|| orders.getPlayer() == null || orders.getPlayer().getId() == null 
        		|| orders.getTurn() == null) 
        {
            throw new BadRequestAlertException("A new orders must have a game, player and turn", ENTITY_NAME, "invalid");
        }
        Orders existing = ordersRepository.findOneByGamePlayerAndTurn(orders.getGame().getId(), orders.getPlayer().getId(), orders.getTurn());
        if (existing != null) {
        	orders.setId(existing.getId());
        }
        Orders result = ordersRepository.save(orders);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /orders : Updates an existing orders.
     *
     * @param orders the orders to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated orders,
     * or with status 400 (Bad Request) if the orders is not valid,
     * or with status 500 (Internal Server Error) if the orders couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/orders")
    @Timed
    public ResponseEntity<Orders> updateOrders(@Valid @RequestBody Orders orders) throws URISyntaxException {
        log.debug("REST request to update Orders : {}", orders);
        if (orders.getId() == null) {
            return createOrders(orders);
        }
        Orders result = ordersRepository.save(orders);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orders.getId().toString()))
            .body(result);
    }

    /**
     * GET  /orders : get all the orders.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of orders in body
     */
    @GetMapping("/orders")
    @Timed
    public List<Orders> getAllOrders() {
        log.debug("REST request to get all Orders");
        return ordersRepository.findAll();
        }

    /**
     * GET  /orders/:id : get the "id" orders.
     *
     * @param id the id of the orders to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orders, or with status 404 (Not Found)
     */
    @GetMapping("/orders/{id}")
    @Timed
    public ResponseEntity<Orders> getOrders(@PathVariable Long id) {
        log.debug("REST request to get Orders : {}", id);
        Orders orders = ordersRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orders));
    }

    /**
     * DELETE  /orders/:id : delete the "id" orders.
     *
     * @param id the id of the orders to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrders(@PathVariable Long id) {
        log.debug("REST request to delete Orders : {}", id);
        ordersRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
