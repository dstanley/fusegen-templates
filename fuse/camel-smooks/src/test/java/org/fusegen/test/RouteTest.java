package @package;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;

import org.junit.Test;

public class RouteTest extends CamelBlueprintTestSupport {
	
    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void testRoute() throws Exception {
        // the route is timer based, so every 5th second a message is send
        // we should then expect at least one message
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);

		System.out.println("If see PojoSR stack on shutdown, its due to https://issues.apache.org/jira/browse/CAMEL-6247, safe to ignore");

        // assert expectations
        assertMockEndpointsSatisfied();
    }

}
