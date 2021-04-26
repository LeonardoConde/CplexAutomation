import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;


public class App {
    public static void main(String[] args){
        FloatCplexAutomation model = new FloatCplexAutomation(5);
        model.setObjectiveEquation(new Double[]{0.2,0.3,0.4,0.5,0.25},'-');
        model.addCondition(new Double[]{30.0,20.0,15.0,80.0,20.0},">=",80.0);
        model.addCondition(new Double[]{60.0,20.0,60.0,20.0,20.0},">=",120.0);
        model.addCondition(new Double[]{5.0,10.0,5.0,3.0,20.0},">=",30.0);
        System.out.println(model.solve());
    }
    public static void mai(String[] args){
        try {
            IloCplex model = new IloCplex();
            IloNumVar x1 = model.numVar(0,Double.MAX_VALUE,"x1");
            IloNumVar x2 = model.numVar(0,Double.MAX_VALUE,"x2");
            IloNumVar x3 = model.numVar(0,Double.MAX_VALUE,"x3");
            IloNumVar x4 = model.numVar(0,Double.MAX_VALUE,"x4");
            IloNumVar x5 = model.numVar(0,Double.MAX_VALUE,"x5");

            IloLinearNumExpr objetivo = model.linearNumExpr();
            objetivo.addTerm(0.2,x1);
            objetivo.addTerm(0.3,x2);
            objetivo.addTerm(0.4,x3);
            objetivo.addTerm(0.5,x4);
            objetivo.addTerm(0.25,x5);

            model.addMinimize(objetivo);

            model.addGe(model.sum(
                    model.prod(30,x1),
                    model.prod(20,x2),
                    model.prod(15,x3),
                    model.prod(80,x4),
                    model.prod(20,x5)
            ),80);

            model.addGe(model.sum(
                    model.prod(60,x1),
                    model.prod(20,x2),
                    model.prod(65,x3),
                    model.prod(20,x4),
                    model.prod(20,x5)
            ),120);

            model.addGe(model.sum(
                    model.prod(5,x1),
                    model.prod(10,x2),
                    model.prod(5,x3),
                    model.prod(3,x4),
                    model.prod(20,x5)
            ),30);

            if(model.solve()){
                System.out.println("Z = "+model.getObjective());
                System.out.println("x1 = "+model.getValue(x1));
                System.out.println("x2 = "+model.getValue(x2));
                System.out.println("x3 = "+model.getValue(x3));
                System.out.println("x4 = "+model.getValue(x4));
                System.out.println("x5 = "+model.getValue(x5));

            }else{
                System.out.println("Sem Solução");
            }
        }catch(IloException e){
            System.out.println(e.getMessage());
        }
    }
}