import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AstarPathing implements PathingStrategy {
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> closed = new ArrayList<>();
        List<Point> open = new ArrayList<>();
        open.add(start);
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);
        Map<Point, Integer> fScore = new HashMap<>();
        fScore.put(start, start.distance(end));
        while (open.size() != 0 ) {

            Point smallest = open.get(0);
            for (Point p : open) {
                if (fScore.get(p) < fScore.get(smallest)) {
                    smallest = p;
                }
            }

            Point current = smallest;
            if (current.adjacent(end)) {
                return reconstruct_path(cameFrom, current, canPassThrough);
            }
            open.remove(current);
            closed.add(current);
            Point neighbors[] = PathingStrategy.CARDINAL_NEIGHBORS.apply(current).filter(canPassThrough).toArray(Point[]::new);
            //List<Point> neighborsFiltered = new ArrayList<Point>(Arrays.asList(neighbors));
            //neighborsFiltered = neighborsFiltered.stream().filter(canPassThrough).collect(Collectors.toList());

        for (Point neighbor : neighbors) {
            if (closed.contains(neighbor)) {
                continue;
            }
            Integer tentative_gScore = gScore.get(current) + current.distance(neighbor);
            if (!(open.contains(neighbor))) {
                open.add(neighbor);
            } else if (tentative_gScore >= gScore.get(neighbor)) {
                continue;
            }
            if(cameFrom.containsKey(neighbor)){
                cameFrom.replace(neighbor, current);
            }else{
                cameFrom.put(neighbor, current);
            }
            if(gScore.containsKey(neighbor)){
                gScore.replace(neighbor, tentative_gScore);
            }else{
                gScore.put(neighbor, tentative_gScore);
            }
            if(fScore.containsKey(neighbor)){
                fScore.replace(neighbor, gScore.get(neighbor) + neighbor.distance(end));
            }else{
                fScore.put(neighbor, gScore.get(neighbor) + neighbor.distance(end));
            }
            if(gScore.get(neighbor)>50){
                return new ArrayList<>();
            }
            /*gScore.put(neighbor, tentative_gScore);
            fScore.put(neighbor, gScore.get(neighbor) + neighbor.distance(end));
            cameFrom.put(neighbor, current);*/


        }
        }
        return null;
    }






    private List<Point> reconstruct_path(Map<Point, Point> cameFrom, Point current, Predicate canPassThrough){
        List<Point> totalPath  = new ArrayList<>();
        totalPath.add(current);
        while(cameFrom.keySet().contains(current)){
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        Collections.reverse(totalPath);


        return totalPath;
}
}
