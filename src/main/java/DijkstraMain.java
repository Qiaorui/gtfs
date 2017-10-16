import service.RouteGenerator;

/**
 * Created by qiaoruixiang on 14/06/2017.
 */
public class DijkstraMain {

    public static void main(String[] args) {


        RouteGenerator generator = new RouteGenerator();
        String route = generator.generateRoute(41.389329, 2.112874, 41.405793, 2.200573, 20160915, 12*3600);
        System.out.println(route);

    }

}
