//Maksim KOZLOV 20219332

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

//Class for point
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
}

/**
 * Class for maze.
 * maze is a 2D array of characters
 */
class Maze1{
    private final int n, m;
    private final Point start;
    private final Point finish;
    private final int[][] maze, used;
    private final int emptyCellsCnt;

    /**
     Constructor to initialize maze
     n - number of rows
     m - number of columns
     start - start point
     finish - finish point
     maze - maze itself
     **/
    public Maze1(int n, int m, Point start, Point finish, int[][] maze){
        this.n = n;
        this.m = m;
        this.start = start;
        this.finish = finish;
        this.maze = maze;
        this.used = new int[m][n];
        this.emptyCellsCnt = countEmptyCells();
    }

    //return number of empty cells in the maze
    private int countEmptyCells(){
        int result = 0;
        for (int y = 0; y < m; y++)
            for (int x = 0; x < n; x++)
                result += 1 - maze[y][x];
        return result;
    }

    //check if point is in the maze
    public boolean isValid(){
        return checkLoops() && allCellsAreReachable();
    }

    //check maze for loops
    //if maze has loops, it is not valid and return false
    private boolean checkLoops(){
        for (int y = 0; y < m; y++){
            for (int x = 0; x < n; x++){
                if (maze[y][x] == 1){
                    Point cur = new Point(x, y);
                    int emptyCnt = getPossibleMoves(cur, true).size();
                    if (emptyCnt == 8)
                        return false;
                }
            }
        }
        return true;
    }

    //check if all cells in the maze is reachable
    private boolean allCellsAreReachable(){
        return dfs(start) == emptyCellsCnt;
    }

    //Main DFS method to find all possible moves from current point
    private int dfs(Point cur){
        //if current point is not empty or already visited, return 0
        if (maze[cur.y][cur.x] == 1 || used[cur.y][cur.x] == 1)
            return 0;
        //mark current point as visited
        used[cur.y][cur.x] = 1;
        //get all possible moves from current point
        List<Point> possibleMoves = getPossibleMoves(cur, false);
        int result = 0;
        //for each possible move, call dfs
        for (Point next : possibleMoves){
            result += dfs(next);
        }
        //return number of reachable cells
        return 1 + result;
    }

    //return list of possible moves from current point
    private List<Point> getPossibleMoves(Point p, boolean allDirections){
        List<Point> result = new ArrayList<>();
        //check all possible moves
        int[] dx = new int[]{-1, 0, 1};
        int[] dy = new int[]{-1, 0, 1};
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                //if current move is not valid, continue
                if (!allDirections && dx[i] * dx[i] + dy[j]* dy[j] != 1) continue;
                Point newPoint = new Point(p.x + dx[i], p.y + dy[j]);
                if (!newPoint.equals(p) && newPoint.x >= 0 && newPoint.x < n && newPoint.y >= 0 && newPoint.y < m &&
                        maze[newPoint.y][newPoint.x] == 0){
                    result.add(newPoint);
                }
            }
        }
        return result;
    }
}

public class q1 {
    public static void main(String[] args) {
        //Read input with pattern useDelimiter("\\D+") to read only numbers from input
        //the "+" means to split on continuous blocks of non-digit characters.
        Scanner scanner = new Scanner(System.in).useDelimiter("\\D+");
        int n = scanner.nextInt(); //number of rows
        int m = scanner.nextInt(); //number of columns
        Point start = new Point(scanner.nextInt() - 1, scanner.nextInt() - 1); //start point
        Point finish = new Point(scanner.nextInt() - 1, scanner.nextInt() - 1); //finish point
        int[][] map = new int[m][n]; //maze itself
        for (int i = 0; i < m; i++){
            for (int j = 0; j < n; j++) {
                map[i][j] = scanner.nextInt(); //read maze
            }
        }
        Maze1 maze = new Maze1(n, m, start, finish, map); //create maze
        if (maze.isValid()) //check if maze is valid
            System.out.println("true");
        else
            System.out.println("false");
    }
}
