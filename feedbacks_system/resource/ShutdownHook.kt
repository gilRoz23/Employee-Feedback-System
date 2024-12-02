import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener

@Component
class ShutdownHook {

    @Autowired
    private lateinit var context: ApplicationContext

    @EventListener(ContextClosedEvent::class)
    fun onShutdown() {
        // Call SpringApplication.exit() to trigger the shutdown process
        SpringApplication.exit(context)
    }
}