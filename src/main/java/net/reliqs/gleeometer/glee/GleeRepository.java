package net.reliqs.gleeometer.glee;

import net.reliqs.gleeometer.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

interface GleeRepository extends PagingAndSortingRepository<Glee, Long>, GleeRepositoryCustom {
    Page<Glee> findAllByUser(User user, Pageable pageable);
}
