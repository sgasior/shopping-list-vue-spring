package pl.edu.kopalniakodu.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.edu.kopalniakodu.domain.*;
import pl.edu.kopalniakodu.repository.BillRepository;
import pl.edu.kopalniakodu.repository.OwnerRepository;
import pl.edu.kopalniakodu.repository.ProductRepository;
import pl.edu.kopalniakodu.repository.TaskRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//@Profile("dev")
@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final OwnerRepository ownerRepository;
    private final ProductRepository productRepository;
    private final BillRepository billRepository;

    @Override
    public void run(String... args) throws Exception {
        generateBill();
        generateProducts();
        generatePersons();
        generateTasks();
    }

    private void generateBill() {
        billRepository.save(new Bill(BilanceType.INCOME, new BigDecimal("123")));
    }

    private void generateProducts() {
        Product product1 = new Product();
        product1.setName("Chleb");
        Product product2 = new Product();
        product2.setName("Mleko");
        Product product3 = new Product();
        product3.setName("Pomidory");
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product1);

    }

    private void generatePersons() {
        Owner owner = new Owner();
        owner.setName("Szymaa");
        UUID savedOwnerID = ownerRepository.save(owner).getId();
        Owner ownerFound = ownerRepository.findById(savedOwnerID).get();
        ownerRepository.save(ownerFound);
    }

    private void generateTasks() {
        Task task = new Task();
        task.setTaskTitle("Zakupy dla Szymka");
        task.setCreatedDate(LocalDateTime.now());
        task.setIsDone(false);

        Task task2 = new Task();
        task2.setTaskTitle("Zakupy dla Anetki");
        task2.setCreatedDate(LocalDateTime.now());
        task2.setIsDone(false);
        taskRepository.save(task);
        taskRepository.save(task2);

        for (Product product : productRepository.findAll()) {
            product.setTask(task);
            productRepository.save(product);
        }

        task.setBill(billRepository.findAll().get(0));
        task.setOwner(ownerRepository.findAll().get(0));
        task2.setOwner(ownerRepository.findAll().get(0));
        taskRepository.save(task);
        taskRepository.save(task2);


    }
}
