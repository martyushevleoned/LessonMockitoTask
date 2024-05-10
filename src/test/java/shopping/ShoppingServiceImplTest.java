package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;

/**
 * Тестирование класса {@link ShoppingServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class ShoppingServiceImplTest {

    private ShoppingServiceImpl shoppingService;
    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        productDao = Mockito.mock(ProductDao.class);
        shoppingService = new ShoppingServiceImpl(productDao);
    }

    /**
     * Нет смысла тестировать метод без логики (геттер/сеттер).
     * Если в создании объекта {@link Cart} существует какая-либо логика,
     * её нужно тестировать в соответствующем классе CartTest.
     */
    @Test
    void getCart() {
    }

    /**
     * Не нужно тестировать БД
     */
    @Test
    void getAllProducts() {
    }

    /**
     * Не нужно тестировать БД
     */
    @Test
    void getProductByName() {
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy}.<br>
     * Проверка уменьшения количества продуктов в магазине при покупке
     * <ul>
     *     <li>Создаём корзину</li>
     *     <li>Создаём продукт, количество которого в магазине 10</li>
     *     <li>Добавляем 7 продуктов в корзину</li>
     *     <li>Проверяем успешность покупки корзины</li>
     *     <li>Проверяем оставшееся количество продуктов в магазине</li>
     *     <li>Проверяем вызов метода сохранения информации о покупке в БД</li>
     * </ul>
     */
    @Test
    void buyOneProduct() throws BuyException {

        Cart cart = new Cart(new Customer(1, "11-11-11"));
        Product product = new Product("productName", 10);
        cart.add(product, 7);

        Assertions.assertTrue(shoppingService.buy(cart));
        Assertions.assertEquals(3, product.getCount());
        Mockito.verify(productDao, Mockito.times(1)).save(Mockito.eq(product));
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy}.<br>
     * Проверка покупки пустой корзины
     * <ul>
     *     <li>Создаём корзину</li>
     *     <li>Покупаем содержимое корзины</li>
     *     <li>Проверяем была ли покупка произведена успешно</li>
     *     <li>Проверяем отсутствие вызова метода сохранения информации о неудачной покупке в БД</li>
     * </ul>
     */
    @Test
    void buyEmptyCart() throws BuyException {

        Cart cart = new Cart(new Customer(1, "11-11-11"));
        Assertions.assertFalse(shoppingService.buy(cart));
        Mockito.verify(productDao, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy}.<br>
     * Проверка покупки всего количества товара
     * <ul>
     *     <li>Создаём корзину</li>
     *     <li>Создаём продукт, количество которого в магазине 10</li>
     *     <li>Добавляем 10 продуктов в корзину</li>
     *     <li>Проверяем успешность покупки корзины</li>
     *     <li>Проверяем оставшееся количество продуктов в магазине</li>
     *     <li>Проверяем вызов метода сохранения информации о покупке в БД</li>
     * </ul>
     */
    @Test
    void buyAllProducts() throws BuyException {

        Cart cart = new Cart(new Customer(1, "11-11-11"));
        Product product = new Product("productName", 10);
        cart.add(product, 10);

        Assertions.assertTrue(shoppingService.buy(cart));
        Assertions.assertEquals(0, product.getCount());
        Mockito.verify(productDao, Mockito.times(1)).save(Mockito.eq(product));
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy}.<br>
     * Проверка покупки двух корзин, количество товара в которых суммарно больше имеющегося
     * <ul>
     *     <li>Создаём 2 корзины</li>
     *     <li>Создаём продукт, количество которого в магазине 15</li>
     *     <li>Добавляем по 10 продуктов в обе корзины</li>
     *     <li>Проверяем успешность покупки первой корзины</li>
     *     <li>Проверяем невозможность покупки второй корзины</li>
     *     <li>Проверяем оставшееся количество продуктов в магазине</li>
     *     <li>Проверяем вызов метода сохранения информации о покупке в БД</li>
     * </ul>
     */
    @Test
    void byuManyCustomers() throws BuyException {

        Cart cart1 = new Cart(new Customer(1, "11-11-11"));
        Cart cart2 = new Cart(new Customer(1, "22-22-22"));
        Product product = new Product("productName", 15);

        cart1.add(product, 10);
        cart2.add(product, 10);

        Assertions.assertTrue(shoppingService.buy(cart1));
        BuyException e = Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart2));
        Assertions.assertEquals("В наличии нет необходимого количества товара 'productName'", e.getMessage());

        Assertions.assertEquals(5, product.getCount());
        Mockito.verify(productDao, Mockito.times(1)).save(Mockito.eq(product));
    }
}