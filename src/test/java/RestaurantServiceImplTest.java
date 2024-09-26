import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import com.FoodApp.FoodOrderingApp.service.RestaurantService;
import com.FoodApp.FoodOrderingApp.service.RestaurantServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RestaurantServiceImplTest.class)
public class RestaurantServiceImplTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemsRepository menuItemsRepository;

    // Automatically inject the mock repositories into the service implementation
    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @InjectMocks
    private RestaurantServiceImpl service;

    @Before("")
    public void setUp() {
        MockitoAnnotations.initMocks(this);  // Initialize mocks before running the test
    }
    
    @Test
    public void testCreateRestaurantSuccess() {
        Restaurant restaurantInput = new Restaurant();
        restaurantInput.setName("Test Restaurant");
        restaurantInput.setCity("Test City");
        restaurantInput.setProcessingCapacity(10);

        RestaurantService service = Mockito.mock(RestaurantService.class);
        Restaurant expectedRestaurant = new Restaurant();
        expectedRestaurant.setName(restaurantInput.getName());
        expectedRestaurant.setAddress(restaurantInput.getAddress());
        expectedRestaurant.setCity(restaurantInput.getCity());
        expectedRestaurant.setProcessingCapacity(restaurantInput.getProcessingCapacity());

        when(service.createRestaurant(restaurantInput)).thenReturn(expectedRestaurant);

        Restaurant actualRestaurant = service.createRestaurant(restaurantInput);

        assertEquals(expectedRestaurant.getName(), actualRestaurant.getName());
        assertEquals(expectedRestaurant.getAddress(), actualRestaurant.getAddress());
        assertEquals(expectedRestaurant.getCity(), actualRestaurant.getCity());
        assertEquals(expectedRestaurant.getProcessingCapacity(), actualRestaurant.getProcessingCapacity());
    }

    @Test
    public void testCreateRestaurantNullInput() {
        RestaurantService service = Mockito.mock(RestaurantService.class);
        service.createRestaurant(null);
    }
    @Test
    public void testGetRestaurantByIdSuccess() throws CustomException {
        Long id = 2L;
        Restaurant expectedRestaurant = new Restaurant();
        expectedRestaurant.setId(id);

        RestaurantRepository mockRepository = Mockito.mock(RestaurantRepository.class);
        when(mockRepository.findById(id)).thenReturn(Optional.of(expectedRestaurant));

        RestaurantService service = new RestaurantServiceImpl();

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setId(2L);
        Menu mockMenu = new Menu();
        mockMenu.setId(1L);
        mockRestaurant.setMenu(mockMenu);

        // Mock the behavior of restaurantRepository.findById()
        when(restaurantRepository.findById(id).orElse(null)).thenReturn(mockRestaurant);


        Restaurant actualRestaurant = service.getRestaurantById(id);

        assertEquals(expectedRestaurant.getId(), actualRestaurant.getId());
    }

    @Test()
    public void testGetRestaurantByIdNotFound() throws CustomException {
        Long id = 1L;

        RestaurantRepository mockRepository = Mockito.mock(RestaurantRepository.class);
        when(restaurantRepository.findById(1L).orElse(null)).thenReturn(null);

        RestaurantService service = new RestaurantServiceImpl();

        service.getRestaurantById(id);
    }
    @Test
    public void testUpdateRestaurantMenuSuccess() throws CustomException {
        Long restaurantId = 1L;
        MenuDTO item = new MenuDTO("Test Item", BigDecimal.TWO);

        RestaurantRepository mockRepository = Mockito.mock(RestaurantRepository.class);
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        when(mockRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        MenuItemsRepository mockMenuItemsRepository = Mockito.mock(MenuItemsRepository.class);

        RestaurantService service = new RestaurantServiceImpl();

        service.updateRestaurantMenu(restaurantId, item);

        Mockito.verify(mockMenuItemsRepository).save(Mockito.any(MenuItem.class));
    }

    @Test()
    public void testUpdateRestaurantMenuRestaurantNotFound() throws CustomException {
        Long restaurantId = 1L;
        MenuDTO item = new MenuDTO("Test Item", BigDecimal.TWO);

        RestaurantRepository mockRepository = Mockito.mock(RestaurantRepository.class);
        when(mockRepository.findById(restaurantId)).thenReturn(Optional.empty());

        RestaurantService service = new RestaurantServiceImpl();

        service.updateRestaurantMenu(restaurantId, item);
    }
}
