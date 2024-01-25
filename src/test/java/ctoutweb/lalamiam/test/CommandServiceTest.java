package ctoutweb.lalamiam.test;

import ctoutweb.lalamiam.model.ProductWithQuantity;
import ctoutweb.lalamiam.model.StoreSchedule;
import ctoutweb.lalamiam.model.dto.*;
import ctoutweb.lalamiam.model.dto.AddProductDto;
import ctoutweb.lalamiam.repository.CommandRepository;
import ctoutweb.lalamiam.repository.CommandProductRepository;
import ctoutweb.lalamiam.repository.ProRepository;
import ctoutweb.lalamiam.repository.builder.StoreEntityBuilder;
import ctoutweb.lalamiam.repository.entity.*;
import ctoutweb.lalamiam.service.CommandService;
import ctoutweb.lalamiam.service.ProService;
import ctoutweb.lalamiam.service.ProductService;
import ctoutweb.lalamiam.service.StoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//TODO vérifier qu'une commande ne puisse pas etre supprimé ou modifié par un store exterieur
@SpringBootTest
public class CommandServiceTest {

  @Autowired
  ProRepository proRepository;

  @Autowired
  CommandService commandService;

  @Autowired
  CommandRepository commandRepository;
  @Autowired
  ProService proService;

  @Autowired
  CommandProductRepository cookRepository;

  @Autowired
  ProductService productService;

  @Autowired
  StoreService storeService;
  HashMap<String, Boolean> testParameters = new HashMap<>();

  //Liste pour commande Pro1
  List<ProductWithQuantity> productsInCommand = new ArrayList<>();
  StoreEntity store;

  @BeforeEach
  void beforeEach() {
    commandRepository.deleteAll();
    proRepository.truncateAll();

    testParameters.clear();
    testParameters.put("isStoreExist", true);
    testParameters.put("isPhoneClientExist", true);
    testParameters.put("isSlotTimeValid", true);
    testParameters.put("isCommandWithProduct", true);
    testParameters.put("isProdutcInStore", true);
    testParameters.put("isProdutcBelongToStore", true);
  }
  @Test
  void should_create_command() {
    // Creation Commande
    CompleteCommandDetailResponseDto addCommand = commandService.addCommand(addCommandSchema());

    // Controle de la commande
    Assertions.assertEquals(1, commandRepository.countAll());
    //Assertions.assertEquals(addCommand.phoneClient(), addCommand.getClientPhone());
    //Assertions.assertEquals(addCommand.getPreparationTime(), addCommand.getPreparationTime());

    // Controle relation produits - store - commande
    Assertions.assertEquals(3, cookRepository.countAll());
    List<CommandProductEntity> commandProducts = cookRepository.findAll();

    Assertions.assertEquals(productsInCommand.get(0).getProductId(), commandProducts.get(0).getProduct().getId());
    Assertions.assertEquals(addCommand.commandId(), commandProducts.get(0).getCommand().getId());
    //Assertions.assertEquals(addCommand  store.getId(), commandProducts.get(0).getStore().getId());

    Assertions.assertEquals(productsInCommand.get(1).getProductId(), commandProducts.get(1).getProduct().getId());
    Assertions.assertEquals(addCommand.commandId(), commandProducts.get(1).getCommand().getId());
    //Assertions.assertEquals(store.getId(), commandProducts.get(1).getStore().getId());

    Assertions.assertEquals(productsInCommand.get(2).getProductId(), commandProducts.get(2).getProduct().getId());
    Assertions.assertEquals(addCommand.commandId(), commandProducts.get(2).getCommand().getId());
    //Assertions.assertEquals(store.getId(), commandProducts.get(2).getStore().getId());
  }

  @Test
  void should_not_create_command_if_store_not_exist() {
    testParameters.put("isStoreExist", false);
    Assertions.assertThrows(RuntimeException.class, ()-> commandService.addCommand(addCommandSchema()));
    Exception exception = Assertions.assertThrows(RuntimeException.class, ()->commandService.addCommand(addCommandSchema()));
    Assertions.assertEquals("Le commerce n'existe pas", exception.getMessage());
  }

  @Test
  void should_not_create_command_if_phone_client_missing() {
    testParameters.put("isPhoneClientExist", false);
    Assertions.assertThrows(RuntimeException.class, ()-> commandService.addCommand(addCommandSchema()));
    Exception exception = Assertions.assertThrows(RuntimeException.class, ()->commandService.addCommand(addCommandSchema()));
    Assertions.assertEquals("Le téléphone client est obligatoire", exception.getMessage());
  }

  @Test
  void should_not_create_command_if_slot_time_invalid() {
    testParameters.put("isSlotTimeValid", false);
    Assertions.assertThrows(RuntimeException.class, ()-> commandService.addCommand(addCommandSchema()));
    Exception exception = Assertions.assertThrows(RuntimeException.class, ()->commandService.addCommand(addCommandSchema()));
    Assertions.assertEquals("La commande ne peut pas être dans le passée", exception.getMessage());
  }

  @Test
  void should_not_create_command_if_command_empty() {
    testParameters.put("isCommandWithProduct", false);
    Assertions.assertThrows(RuntimeException.class, ()-> commandService.addCommand(addCommandSchema()));
    Exception exception = Assertions.assertThrows(RuntimeException.class, ()->commandService.addCommand(addCommandSchema()));
    Assertions.assertEquals("La commande ne peut pas être vide", exception.getMessage());

  }

  @Test
  void should_not_create_command_if_product_not_register() {
    testParameters.put("isProdutcInStore", false);
    Assertions.assertThrows(RuntimeException.class, ()-> commandService.addCommand(addCommandSchema()));
    Exception exception = Assertions.assertThrows(RuntimeException.class, ()->commandService.addCommand(addCommandSchema()));
    Assertions.assertEquals("Le produit n'existe pas", exception.getMessage());

  }

  @Test
  void should_not_create_command_if_product_not_belong_to_store() {
    testParameters.put("isProdutcBelongToStore", false);
    Assertions.assertThrows(RuntimeException.class, ()-> commandService.addCommand(addCommandSchema()));
    Exception exception = Assertions.assertThrows(RuntimeException.class, ()->commandService.addCommand(addCommandSchema()));
    Assertions.assertEquals("Certains produits à ajouter ne sont pas rattachés au commerce", exception.getMessage());
  }

  @Test
  void should_update_product_quantity_in_command(){
    // Creation Commande
    CompleteCommandDetailResponseDto addCommand = commandService.addCommand(addCommandSchema());

    // Récuperation d'un produit de la commande
    BigInteger productChangeId = productsInCommand.get(0).getProductId();

    // Modification du produit dans la commande
    UpdateProductQuantityDto updateCommandSchema = new UpdateProductQuantityDto(
            productChangeId, addCommand.commandId(), store.getId(), 4);
    UpdateProductQuantityResponseDto productUpdated = commandService.updateProductQuantityInCommand(updateCommandSchema);

    Assertions.assertEquals(4, productUpdated.productInCommand().getProductQuantity());

    // Vérification id
    Assertions.assertEquals(productChangeId, productUpdated.productInCommand().getProductId());

    // Vérification du nouveau prix
    Assertions.assertEquals(140, productUpdated.commandPrice());

    // Verification du nouveau temps de prepapration de la commande
    Assertions.assertEquals(80, productUpdated.commandPreparationTime());
  }

  @Test
  void should_not_update_product_quantity_if_product_not_belong_to_command() {
    // Creation Commande store1
    CompleteCommandDetailResponseDto addCommand = commandService.addCommand(addCommandSchema());

    // Creation produit pour store2
    ProInformationDto pro2 = createPro();
    StoreEntity store2 = createStore(pro2);
    List<AddProductResponseDto> productStore2 = createProduct(store2.getId());

    // Récuperation d'un produit du store2
    BigInteger productStore2Id = productStore2.get(0).id();

    // Modification quantité commande store1 avec un produit du store2
    UpdateProductQuantityDto updateCommandSchema = new UpdateProductQuantityDto(
            productStore2Id, addCommand.commandId(), store.getId(), 12);

    Exception exception = Assertions.assertThrows(
            RuntimeException.class,
            ()->commandService.updateProductQuantityInCommand(updateCommandSchema)
    );
    Assertions.assertEquals("Certains produits à modifier ne sont pas rattachés au commerce", exception.getMessage());


  }

  @Test
  void should_force_update_product_quantity_to_1_if_quantity_inferior_at_1() {
    // Creation Commande
    CompleteCommandDetailResponseDto addCommand = commandService.addCommand(addCommandSchema());

    // Récuperation d'un produit de la commande
    BigInteger productChangeId = productsInCommand.get(0).getProductId();

    // Modification du produit dans la commande
    UpdateProductQuantityDto updateCommandSchema = new UpdateProductQuantityDto(
            productChangeId, addCommand.commandId(), store.getId(), 0);
    UpdateProductQuantityResponseDto productUpdated = commandService.updateProductQuantityInCommand(updateCommandSchema);

    Assertions.assertEquals(1, productUpdated.productInCommand().getProductQuantity());
    Assertions.assertEquals(productChangeId, productUpdated.productInCommand().getProductId());
  }

  @Test
  void should_delete_one_product_in_command() {
    // Creation Commande
    CompleteCommandDetailResponseDto addCommand = commandService.addCommand(addCommandSchema());

    // Recuperation du produit a supprimer - Prix 10€ - temps prepaparation 5 min
    BigInteger productId = addCommand.productInCommandList().get(0).getProductId();

    // Produits restants
    // Quantite 2 - AddProductSchema addProductSchema2 = new AddProductSchema("coco", 20D, "initial description", 10, "s", storeId);
    // Quantite 2 - AddProductSchema addProductSchema1 = new AddProductSchema("lait", 10D, "initial description", 5, "s", storeId);

    BigInteger commandId = addCommand.commandId();
    // Suppression du produit
    DeleteProductInCommandDto deleteProductInCommand = new DeleteProductInCommandDto(commandId, productId, store.getId());
    SimplifyCommandDetailResponseDto updateCommandDetail = commandService.deleteProductInCommand(deleteProductInCommand);

    // Vérification nombre de produit
    Assertions.assertEquals(2, updateCommandDetail.productInCommandList().size());

    // Vérification produit supprimé absent
    Assertions.assertEquals(0,
            updateCommandDetail.productInCommandList()
                    .stream()
                    .filter(product -> product.getProductId() == productId)
                    .collect(Collectors.toList())
                    .size());

    // Vérification du nouveau prix
    Assertions.assertEquals(100, updateCommandDetail.commandPrice());

    // Verification du nouveau temps de prepapration de la commande
    Assertions.assertEquals(60, updateCommandDetail.commandPreparationTime());
  }

  @Test
  void should_add_one_or_multiple_products_to_command() {
    // TODO 1 commande ne oeut pas etre modifié si pas existante
    // Creation Commande
    CompleteCommandDetailResponseDto addCommand = commandService.addCommand(addCommandSchema());

    // Récuperation produitId a ajouter
    List<BigInteger> productIdList = new ArrayList<>();
    BigInteger productId = productsInCommand.get(0).getProductId();
    productIdList.add(productId);
    AddProductsInCommandDto addProductsInCommandSchema = new AddProductsInCommandDto(store.getId(), productIdList, addCommand.commandId());
    SimplifyCommandDetailResponseDto commandDetail = commandService.addProductsInCommand(addProductsInCommandSchema);

    // Vérification nombre de produit
    Assertions.assertEquals(7, commandDetail.numberOProductInCommand());

    // Vérification du nouveau prix
    Assertions.assertEquals(130, commandDetail.commandPrice());

    // Verification du nouveau temps de prepapration de la commande
    Assertions.assertEquals(75, commandDetail.commandPreparationTime());

  }

  @Test
  void should_add_one_product_already_in_command() {
    // Creation Commande
    CompleteCommandDetailResponseDto addCommand = commandService.addCommand(addCommandSchema());

    // Récuperation produitId a ajouter
    List<BigInteger> productIdList = new ArrayList<>();
    BigInteger productId = productsInCommand.get(0).getProductId();

    productIdList.add(productId);
    AddProductsInCommandDto addProductsInCommandSchema = new AddProductsInCommandDto(store.getId(), productIdList, addCommand.commandId());
    SimplifyCommandDetailResponseDto commandDetail = commandService.addProductsInCommand(addProductsInCommandSchema);

    // Vérification que le produit ajouté n'est pas en doublons
    List<ProductWithQuantity> findProductIdInProductList = commandDetail
            .productInCommandList()
            .stream()
            .filter(product-> product.getProductId().equals(productId)).collect(Collectors.toList());
    Assertions.assertEquals(1, findProductIdInProductList.size());
    Assertions.assertEquals(3, findProductIdInProductList.stream().filter(product->product.getProductId().equals(productId)).findFirst().get().getProductQuantity());
  }

  @Test
  void should_find_first_slot_available_for_command() {
    // Creation store - produit - commande
    ProInformationDto pro = createPro();
    StoreEntity store = createStore(pro);
    List<AddProductResponseDto> createProductList = createProduct(store.getId());
    List<ProductWithQuantity> products = createProductsInCommand(createProductList);

    LocalDateTime tomorrow = LocalDateTime.now().plusDays(2);
    LocalDateTime now = LocalDateTime.now().plusDays(1);
    LocalDateTime yesterday = LocalDateTime.now();

    // Creation Commande
    LocalDateTime time1 = LocalDateTime.of(now.getYear(), now.getMonth() ,now.getDayOfMonth(),20,55,00);
    LocalDateTime time2 = LocalDateTime.of(now.getYear(), now.getMonth() ,now.getDayOfMonth(),19,00,00);
    LocalDateTime time3 = LocalDateTime.of(now.getYear(), now.getMonth() ,now.getDayOfMonth(),20,30,00);
    LocalDateTime time4 = LocalDateTime.of(now.getYear(), now.getMonth() ,now.getDayOfMonth(),18,30,00);
    LocalDateTime time5 = LocalDateTime.of(now.getYear(), now.getMonth() ,now.getDayOfMonth(),19,30,00);

    // OldCommannd
    LocalDateTime timeOld = LocalDateTime.of(yesterday.getYear(), yesterday.getMonth() ,yesterday.getDayOfMonth(),23,55,00);

    // Future
    LocalDateTime timeFuture = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth() ,tomorrow.getDayOfMonth(),20,55,00);

    commandService.addCommand(customCommandSchema(store.getId(), time1));
    commandService.addCommand(customCommandSchema(store.getId(), time2));
    commandService.addCommand(customCommandSchema(store.getId(), time3));
    commandService.addCommand(customCommandSchema(store.getId(), time4));
    commandService.addCommand(customCommandSchema(store.getId(), time5));
    commandService.addCommand(customCommandSchema(store.getId(), timeOld));
    commandService.addCommand(customCommandSchema(store.getId(), timeFuture));

    // Date de la commande
    LocalDate commandDate = LocalDate.from(now);

    // Date de consultation des Slot
    LocalDateTime consultationDate = commandDate.atTime(11, 10,50);

    //Temps de prépa commande
    int preparationTime = 10;

    ////////////////////////

    Integer commandPreparationTime = 5;
    List<LocalDateTime> findAllSlotAvailable = commandService.findAllSlotAvailable(
            new FindListOfSlotTimeAvailableDto(
                    commandDate,
                    store.getId(),
                    preparationTime,
                    consultationDate)
    );
    Assertions.assertEquals(10, findAllSlotAvailable.size());

  }

  /**
   * Création schema pour une commande
   * @return AddCommandSchema
   */
  private AddCommandDto addCommandSchema() {

    // Parametrages des tests
    boolean isStoreExist = testParameters.get("isStoreExist");
    boolean isPhoneClientExist = testParameters.get("isPhoneClientExist");
    boolean isSlotTimeValid = testParameters.get("isSlotTimeValid");
    boolean isCommandWithProduct = testParameters.get("isCommandWithProduct");
    boolean isProductInStore = testParameters.get("isProdutcInStore");
    boolean isProdutcBelongToStore = testParameters.get("isProdutcBelongToStore");

    // Creation Pro
    ProInformationDto createdPro = createPro();
    ProInformationDto createdPro2;

    // Creation Store
    StoreEntity createdStore = createStore(createdPro);
    StoreEntity createdStore2;

    store = isStoreExist ?
            createdStore :
            StoreEntityBuilder.aStoreEntity().withId(BigInteger.valueOf(0)).build();

    // Création produits
    List<AddProductResponseDto> createProductList = createProduct(createdStore.getId());

    // Creation commande
    createProductsInCommand(createProductList);

    // Creation Pro2 + Store2 + produits2 et ajout d'un produit à la commande 1
    if(!isProdutcBelongToStore) {
      createdPro2 = createPro();
      createdStore2 = createStore(createdPro2);
      List<AddProductResponseDto> createProductList2 = createProduct(createdStore2.getId());
      ProductWithQuantity productStore2 = new ProductWithQuantity(createProductList2.get(0).id(), 1);
      productsInCommand.add(productStore2);
    }

    // Création commande
    String phoneCient = isPhoneClientExist ?
            "0623274102" :
            "";
    LocalDateTime commandSlotTime = isSlotTimeValid ?
            LocalDateTime.now().plusHours(1) :
            LocalDateTime.of(2022, 10,10,23,55,10);


    if(!isCommandWithProduct) productsInCommand.clear();

    if(!isProductInStore) productsInCommand.add(new ProductWithQuantity(BigInteger.valueOf(0), 1));

    AddCommandDto addCommandSchema = new AddCommandDto(
            phoneCient,
            commandSlotTime,
            store.getId(),
            productsInCommand);

    // schema ajout commande
    return addCommandSchema;
  }

  private AddCommandDto customCommandSchema(BigInteger storeId, LocalDateTime slotTime) {
    return new AddCommandDto(
            "phoneCient",
            slotTime,
            storeId,
            productsInCommand);
  }
  /**
   * Liste des produits pour une commande
   * @param productList
   * @return
   */
  public List<ProductWithQuantity> createProductsInCommand(List<AddProductResponseDto> productList) {
    productsInCommand = productList
            .stream()
            .map(product -> new ProductWithQuantity(product.id(), 2))
            .collect(Collectors.toList());

    return productsInCommand;
  }


  /**
   * Creation Professionnel
   * @return ProInformationDto
   */
  public ProInformationDto createPro() {
    ProInformationDto createdPro = proService.addProfessional(new AddProfessionalDto("", "password", "aaa"));
    return createdPro;
  }

  /**
   * Creation Store
   * @param createdPro - ProInformationDto - Données sur le professionnel
   * @return StoreEntity
   */
  public StoreEntity createStore(ProInformationDto createdPro) {

    // horaires store
    List<StoreSchedule> storeSchedules = List.of(
            new StoreSchedule(LocalTime.of(11,30,00), LocalTime.of(14,00,00)),
            new StoreSchedule(LocalTime.of(18,30,00), LocalTime.of(22,00,00))
    );

    AddStoreDto addStoreSchema = new AddStoreDto(
            createdPro.id(),
            "magasin",
            "rue des carriere",
            "auterive",
            "31190",
            storeSchedules,
            10);
    StoreEntity createdStore = storeService.createStore(addStoreSchema);
    return createdStore;
  }
  public List<AddProductResponseDto> createProduct(BigInteger storeId) {
    AddProductDto addProductSchema1 = new AddProductDto("lait", 10D, "initial description", 5, "s", storeId);
    AddProductDto addProductSchema2 = new AddProductDto("coco", 20D, "initial description", 10, "s", storeId);
    AddProductDto addProductSchema3 = new AddProductDto("orange", 30D, "initial description", 20, "s", storeId);

    AddProductResponseDto addProduct1 =  productService.addProduct(addProductSchema1);
    AddProductResponseDto addProduct2 =  productService.addProduct(addProductSchema2);
    AddProductResponseDto addProduct3 =  productService.addProduct(addProductSchema3);

    List<AddProductResponseDto> createdProductList = new ArrayList<>();
    createdProductList.add(addProduct1);
    createdProductList.add(addProduct2);
    createdProductList.add(addProduct3);

    return createdProductList;
  }



}