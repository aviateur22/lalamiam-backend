package ctoutweb.lalamiam.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import ctoutweb.lalamiam.model.dto.AddProductDto;
import ctoutweb.lalamiam.repository.ProductRepository;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "product")
public class ProductEntity {
  @Id
  @SequenceGenerator(name="productPkSeq", sequenceName="PRODUCT_PK_SEQ", allocationSize=1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "productPkSeq")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "price")
  private Double price;

  @Column(name = "description")
  private String description;

  @Column(name = "preparation_time")
  private Integer preparationTime;

  @Column(name = "photo")
  private String photo;

  @Column(name = "is_avail")
  private Boolean isAvail;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "store_id")
  private StoreEntity store;

  @OneToMany(mappedBy = "product")
  Set<CommandProductEntity> commandProducts;

  /**
   * Recherche un produit
   * @param productRepository
   * @return
   * @throws RuntimeException
   */
  public ProductEntity findProductById(ProductRepository productRepository) throws RuntimeException {
    return productRepository
            .findById(id)
            .orElseThrow(()->new RuntimeException("Le produit n'existe pas"));
  }

  /**
   * Trouve le prix d'un produit
   * @param productRepository
   * @return
   * @throws RuntimeException
   */
  public Double findProductPrice(ProductRepository productRepository) throws RuntimeException {
    return findProductById(productRepository).getPrice();
  }


 //////////////////////////////////////////////////////////////////////////

  public ProductEntity() {
  }

  public ProductEntity(
          Long id,
          String name,
          Double price,
          String description,
          Integer preparationTime,
          String photo,
          Boolean isAvail
  ) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.description = description;
    this.preparationTime = preparationTime;
    this.photo = photo;
    this.isAvail = isAvail;
  }

  public ProductEntity(Long productId) {
    this.id = productId;
  }

  public ProductEntity(AddProductDto addProductSchema) {
    this.name = addProductSchema.name();
    this.photo = addProductSchema.photo();
    this.preparationTime = addProductSchema.preparationTime();
    this.description = addProductSchema.description();
    this.price = addProductSchema.price();
    this.isAvail = true;
    this.store = new StoreEntity(addProductSchema.storeId());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getPreparationTime() {
    return preparationTime;
  }

  public void setPreparationTime(Integer preparationTime) {
    this.preparationTime = preparationTime;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @JsonBackReference
  public StoreEntity getStore() {
    return store;
  }

  public void setStore(StoreEntity store) {
    this.store = store;
  }

  @JsonManagedReference
  public Set<CommandProductEntity> getCommandProducts() {
    return commandProducts;
  }

  public void setCommandProducts(Set<CommandProductEntity> commandProducts) {
    this.commandProducts = commandProducts;
  }

  public Boolean getIsAvail() {
    return isAvail;
  }

  public void setIsAvail(Boolean avail) {
    isAvail = avail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProductEntity product = (ProductEntity) o;
    return Objects.equals(id, product.id) && Objects.equals(name, product.name) && Objects.equals(price, product.price) && Objects.equals(description, product.description) && Objects.equals(preparationTime, product.preparationTime) && Objects.equals(photo, product.photo) && Objects.equals(createdAt, product.createdAt) && Objects.equals(updatedAt, product.updatedAt) && Objects.equals(store, product.store) && Objects.equals(commandProducts, product.commandProducts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, price, description, preparationTime, photo, createdAt, updatedAt, store, commandProducts);
  }

  @Override
  public String toString() {
    return "ProductEntity{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", description='" + description + '\'' +
            ", preparationTime=" + preparationTime +
            ", photo='" + photo + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", store=" + store +
            //", cooks=" + cooks +
            '}';
  }
}
