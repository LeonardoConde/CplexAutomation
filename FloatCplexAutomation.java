import ilog.concert.*;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class FloatCplexAutomation {
    private IloNumVar[] xs;
    private IloCplex model;
    private IloLinearNumExpr objective;
    private Boolean objectiveSetted = false;
    private Boolean conditionSetted = false;

    FloatCplexAutomation(int amountOfVariables){
        try{
            model = new IloCplex();
            xs = new IloNumVar[amountOfVariables];
            objective = model.linearNumExpr();
            for(int i= 0; i<amountOfVariables;i++){
              xs[i] = model.numVar(0,Double.MAX_VALUE,("x"+(i+1)));
            }
        }catch(IloException e){
            exception(e);
        }
    }

    FloatCplexAutomation(int amountOfVariables,Double[] expoents, char type){
        Boolean validType = ((type=='+')||(type=='-'));
        if(validType&&(expoents.length==amountOfVariables)){
            try{
                model = new IloCplex();
                xs = new IloNumVar[amountOfVariables];
                objective = model.linearNumExpr();
                for(int i= 0; i<amountOfVariables;i++){
                    xs[i] = model.numVar(0,Double.MAX_VALUE,("x"+(i+1)));
                }
                for (int i = 0; i < xs.length; i++) {
                    objective.addTerm(expoents[i], xs[i]);
                }
                switch(type){
                    case '+':
                        model.addMaximize(objective);
                        break;
                    case '-':
                        model.addMinimize(objective);
                        break;
                }
                objectiveSetted=true;
            }catch(IloException e){
                exception(e);
            }
        }else{
            System.out.println("Error: "+((validType==false)?(type)+" is not a valid type!":"invalid amount of expoents!"));
        }
    }


    public void setObjectiveEquation(Double[] expoents, char type){
        Boolean validType = ((type=='+')||(type=='-'));
        if(validType&&(expoents.length==xs.length)){
            try {
                for (int i = 0; i < xs.length; i++) {
                    objective.addTerm(expoents[i], xs[i]);
                }
                switch(type){
                    case '+':
                        model.addMaximize(objective);
                        break;
                    case '-':
                        model.addMinimize(objective);
                        break;
                }
                objectiveSetted=true;
            } catch (IloException e) {
                exception(e);
            }
        }else{
            System.out.println("Error: "+((validType==false)?(type)+" is not a valid type!":"invalid amount of expoents!"));
        }
    }

    public void addCondition(Double[] expoents,String operation,Double result){
        boolean validOperation =(operation.equals("=="))||(operation.equals("<="))||(operation.equals(">="));
        IloNumExpr[] expr;
        if(objectiveSetted==true){
            try{
                if(validOperation&&(expoents.length==xs.length)){
                    expr = new IloNumExpr[xs.length];
                    for(int i =0;i< xs.length;i++) {
                        expr[i]= model.prod(expoents[i],this.xs[i]);
                    }
                    switch (operation){
                        case "==":
                            model.addEq(model.sum(expr),result);
                            break;

                        case "<=":
                            model.addLe(model.sum(expr),result);
                            break;

                        case ">=":
                            model.addGe(model.sum(expr),result);
                            break;
                    }
                    conditionSetted = true;

                }else{
                    System.out.println("Error: "+((validOperation==false)?(operation)+" is not a valid operation!":"invalid amount of expoents!"));
                }
            }catch (IloException e){
                exception(e);
            }
        }else{
            System.out.println("The objective function has not been declared");
        }
    }
    public String solve(){
        String result = null;
        if(conditionSetted==true) {
            try {
                if (model.solve()) {
                    result = model.getObjective().toString() + "\n";
                    for (int i = 0; i < xs.length; i++) {
                        result = result + "x" + (i + 1) + " = " + model.getValue(xs[i]) + ";\n";
                    }
                } else {
                    result = "Unresponsive system";
                }

            } catch (IloException e) {
                exception(e);

            }
        }else{
            System.out.println("Any condition has not been declared");
        }

        return result;
    }

    private void exception(IloException e){
        System.out.println("Error:");
        System.out.println(e.getMessage());
        e.printStackTrace();
    }

}

