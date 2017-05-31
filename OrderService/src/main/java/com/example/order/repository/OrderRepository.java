package com.example.order.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.order.vo.Order;

@RepositoryRestResource
public interface OrderRepository extends CrudRepository<Order, Long>{

}
