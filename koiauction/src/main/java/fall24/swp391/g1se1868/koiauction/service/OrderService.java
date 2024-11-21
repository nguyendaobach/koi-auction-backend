package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import fall24.swp391.g1se1868.koiauction.repository.OrderRepository;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    public Order addOrder(Integer auctionId, OrderRequest orderRequest, Integer userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        if(auction.getFinalPrice()==null||auction.getWinnerID()==null||!auction.getStatus().equalsIgnoreCase("Paid")){
            throw new RuntimeException("Auction status is not paid");
        }
        if(auction.getWinnerID()!=userId){
            throw new RuntimeException("User is not the winner ID");
        }
        if(orderRepository.findOrderByAuctionId(auctionId)!=null){
            throw new RuntimeException("Order for auction already exists");
        }
        User bidder = userRepository.findById(auction.getWinnerID())
                .orElseThrow(() -> new RuntimeException("Bidder not found"));
        Order order = new Order();
        order.setAuctionID(auction);
        order.setBidderID(bidder);
        order.setAddress(orderRequest.getAddress());
        order.setDate(Instant.now());
        order.setPrice(auction.getFinalPrice());
        order.setPhoneNumber(orderRequest.getPhoneNumber());
        order.setNote(orderRequest.getNote());
        order.setFullName(orderRequest.getFullName());
        order.setStatus("Pending");
        return orderRepository.save(order);
    }
    public Order updateOrder(Integer orderId, OrderRequest orderRequest, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getBidderID().getId().equals(userId)) {
            throw new RuntimeException("User is not authorized to update this order");
        }
        if (!order.getStatus().equalsIgnoreCase("Pending")) {
            throw new RuntimeException("Order cannot be updated as it is not in a 'Pending' state");
        }
        order.setAddress(orderRequest.getAddress());
        order.setPhoneNumber(orderRequest.getPhoneNumber());
        order.setNote(orderRequest.getNote());
        order.setFullName(orderRequest.getFullName());
        return orderRepository.save(order);
    }

    public List<OrderResponse> getOrdersByUser(Integer userId) {
        List<Order> ordersAsBidder = orderRepository.findOrdersByBidderId(userId);
        List<Order> ordersAsBreeder = orderRepository.findOrdersByBreederId(userId);
        List<Order> combinedOrders = new ArrayList<>();
        combinedOrders.addAll(ordersAsBidder);
        combinedOrders.addAll(ordersAsBreeder);

        return combinedOrders.stream().map(order -> new OrderResponse(
                order.getId(),
                order.getBidderID() != null ? order.getBidderID().getId() : null,
                order.getAuctionID() != null ? order.getAuctionID().getId() : null,
                order.getAddress(),
                order.getDate(),
                order.getPrice(),
                order.getPhoneNumber(),
                order.getNote(),
                order.getStatus()
        )).toList();
    }
    public OrderResponse getOrderById(Integer orderId, Integer userId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (!optionalOrder.isPresent()) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();
        if ((order.getBidderID() != null && order.getBidderID().getId().equals(userId))) {
            if (!order.getStatus().equals("Pending")) {
                throw new AccessDeniedException("This order cannot be modified because it is no longer Pending.");
            }
            return new OrderResponse(
                    order.getId(),
                    order.getBidderID() != null ? order.getBidderID().getId() : null,
                    order.getAuctionID() != null ? order.getAuctionID().getId() : null,
                    order.getAddress(),
                    order.getDate(),
                    order.getPrice(),
                    order.getPhoneNumber(),
                    order.getNote(),
                    order.getStatus()
            );
        } else {
            throw new AccessDeniedException("User does not have permission to access this order");
        }
    }
    public String changeStatusToShipping(Integer orderId, Integer userId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (!optionalOrder.isPresent()) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();
        if(order.getAuctionID().getId()!=userId){
            throw new RuntimeException("User is not authorized to update this order");
        }else {
            order.setStatus("Shipping");
            orderRepository.save(order);
            return "Updated successfully";
        }
    }
    public String changeStatusToDispute(Integer orderId, Integer userId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (!optionalOrder.isPresent()) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();
        if(order.getBidderID().getId()!=userId){
            throw new RuntimeException("User is not authorized to update this order");
        }
        order.setStatus("Dispute");
        orderRepository.save(order);
        return "Updated successfully";
    }
    @Transactional
    public String doneOrder(Integer orderId, Integer userId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (!optionalOrder.isPresent()) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        Order order = optionalOrder.get();
        if(order.getBidderID().getId()!=userId){
            throw new RuntimeException("User is not authorized to update this order");
        }
        order.setStatus("Done");
        WalletService walletService = new WalletService();
        Auction auction = order.getAuctionID();
        walletService.refund(auction.getBreederID(),auction.getFinalPrice()+auction.getBreederDeposit(),order.getAuctionID().getId());
        auction.setStatus("Finished");
        orderRepository.save(order);
        return "Updated successfully";
    }
    @Transactional
    public String rejectOrder(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        if (!order.getBidderID().getId().equals(userId)) {
            throw new RuntimeException("User is not authorized to update this order");
        }
        order.setStatus("UnSuccessful");
        Auction auction = order.getAuctionID();
        auction.setStatus("Finished");
        Long breederRefundAmount = auction.getBreederDeposit();
        Long bidderRefundAmount = auction.getFinalPrice();
        WalletService walletService = new WalletService();
        walletService.refund(auction.getBreederID(), breederRefundAmount, auction.getId());
        walletService.refund(auction.getWinnerID(), bidderRefundAmount, auction.getId());
        orderRepository.save(order);

        return "Order refund processed for Bidder and Breeder.";
    }

    @Transactional
    public List<Order> findOrderByStatus() {
        List<Order> orders = orderRepository.findOrderByStatus();
        for (Order order : orders) {
            if (order.getBidderID() != null) {
                Hibernate.initialize(order.getBidderID());
            }
            if (order.getAuctionID() != null) {
                Hibernate.initialize(order.getAuctionID());
            }
        }
        return orders;
    }

    public List<OrderResponse> getAllOrder() {

        List<Order> orderr=orderRepository.findAll();
        List<Order> combinedOrders = new ArrayList<>();
        combinedOrders.addAll(orderr);

        return combinedOrders.stream().map(order -> new OrderResponse(
                order.getId(),
                order.getBidderID() != null ? order.getBidderID().getId() : null,
                order.getAuctionID() != null ? order.getAuctionID().getId() : null,
                order.getAddress(),
                order.getDate(),
                order.getPrice(),
                order.getPhoneNumber(),
                order.getNote(),
                order.getStatus()
        )).toList();
    }
}
