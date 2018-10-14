import it.menzani.logger.api.Logger;
import it.menzani.logger.impl.ConsoleConsumer;
import it.menzani.logger.impl.Pipeline;
import it.menzani.logger.impl.SynchronousLogger;

import java.util.UUID;

class ProfiledLoggerTest {
    public static void main(String[] args) {
        Logger logger = new SynchronousLogger()
                .addPipeline(new Pipeline()
                        .addConsumer(new ConsoleConsumer()))
                .profiled();
        logger.info(UUID.randomUUID());
        logger.info(UUID.randomUUID());
    }
}
