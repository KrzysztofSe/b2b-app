package b2b.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document
public class Basket {

    @Id
    private String id;

    @JsonIgnore
    private BasketStatus status = BasketStatus.PENDING;

    @JsonIgnore
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    private Set<Product> products = new HashSet<>();

    public Basket() {}

    public Basket(String id, BasketStatus status, Set<Product> products) {
        this.id = id;
        this.status = status;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BasketStatus getStatus() {
        return status;
    }

    public void setStatus(BasketStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basket basket = (Basket) o;
        return Objects.equals(id, basket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Basket{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", lastModifiedDate=" + lastModifiedDate +
                ", products=" + products +
                '}';
    }
}
