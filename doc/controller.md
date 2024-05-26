# Controller
The main logic of the game will be written inside a Controller. So here's an example of a very minimal Controller.
```java
public class MainController extends Controller {

    private Canvas canvas;

    @Override
    public void init(EngineContext context) {
        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                
                // Drawing code
                
            }
        };
        windowProvider.addComponent(test);
    }

    @Override
    public void update() {
        // Some logic
        
        canvas.repaint();
    }

    @Override
    public void cleanup() {
        // Any cleanup
    }

}
```