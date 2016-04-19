package com.byteflair.resthooks.subscriptions;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, String>{
}
