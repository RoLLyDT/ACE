//Maksim KOZLOV 20219332

import java.util.*;

class Point{
    final public int x, y;

    //Constructor to initialize point
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    //Method "equals" changed via @Override to check if two points are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    //return hash code of the point
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    //return string representation of the point uses in debugging
    @Override
    public String toString(){
        return String.valueOf(x) + ":" + y;
    }
}

/**
 * Class for maze.
 * maze is a 2D array of characters
 */
class Maze{
    private final int n, m, k;
    private final Point start;
    private final Point finish;
    private final int[][] maze;

    /**
    Constructor to initialize maze
    n - number of rows
    m - number of columns
    k - number of keys
    start - start point
    finish - finish point
    maze - maze itself
    **/
    public Maze(int n, int m, Point start, Point finish, int[][] maze, int k){
        this.n = n;
        this.m = m;
        this.k = k;
        this.start = start;
        this.finish = finish;
        this.maze = maze;
    }

    //used to call bfs method from main
    public int solve(){
        return bfs();
    }

    //Main BFS method to find all possible paths
    private int bfs(){
        //queue to store possible paths
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(start);
        //map to store all possible paths
        Map<Point, Point> path = new HashMap<>();
        path.put(start, null); //start point has no parent
        while (!queue.isEmpty()){ //while queue is not empty
            Point cur = queue.poll(); //get first element from queue
            for (Point next : getPossibleMoves(cur)){ //for each possible move
                if (!path.containsKey(next)) { //if path doesn't contain this move
                    queue.add(next); //add it to queue
                    path.put(next, cur); //add it to path
                }
            }
        }
        if (!path.containsKey(finish)) //if finish point is not in path return -1
            return -1;
        int result = 0;
        Point cur = finish;
        while (cur != start && path.containsKey(cur)) {
            //Way back, while current point is not start
            //point and path contains current point
            //get parent of current point
            cur = path.get(cur);
            result++;
        }
        return result;
    }

    //return list of possible moves from current point (with walls)
    private List<Point> getPossibleMoves(Point p){
        List<Point> result = new ArrayList<>();
        int[] dx = new int[]{-1, 0, 1};
        int[] dy = new int[]{-1, 0, 1};
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (dx[i] * dx[i] + dy[j]* dy[j] != 1) continue; //if not adjacent continue

                int wallCnt = 0;
                int t = 1;

                //check if there is a wall
                Point newPoint = new Point(p.x + dx[i] * t, p.y + dy[j] * t);
                while (pointIsValid(newPoint) && (wallCnt <= k || k == 0)){ //while point is valid and wall count is less than k or k is 0
                    if (maze[newPoint.y][newPoint.x] == 0) //if point is empty
                        result.add(newPoint); //add point to result
                    else
                        wallCnt++;
                    if (k == 0) break;
                    t++;
                    newPoint = new Point(p.x + dx[i] * t, p.y + dy[j] * t);
                }
            }
        }
        return result;
    }

    private boolean pointIsValid(Point p){
        return p.x >= 0 && p.x < n && p.y >= 0 && p.y < m;
    }
}

//Main class
public class q2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useDelimiter("\\D+");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Point start = new Point(scanner.nextInt() - 1, scanner.nextInt() - 1);
        Point finish = new Point(scanner.nextInt() - 1, scanner.nextInt() - 1);
        int k = scanner.nextInt();
        int[][] map = new int[m][n];
        for (int i = 0; i < m; i++){
            for (int j = 0; j < n; j++) {
                map[i][j] = scanner.nextInt();
            }
        }
        Maze maze = new Maze(n, m, start, finish, map, k);//initialize maze
        System.out.println(maze.solve());
    }
}
