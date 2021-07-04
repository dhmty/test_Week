/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.test_week1;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

/**
 *
 * @author dhmty
 */
public class MIP {
    
    public static void main(String args[]) {
        // TODO code application logic here
        Loader.loadNativeLibraries();
        // Data
        // [START data_model]
        String [] NV={"A","B","C","D"};
        double[]CV = {18,52,64,39,75,55,19,48,35,57,8,65,27,25,14,16};
        int numNV=NV.length;
        int numCV=CV.length/numNV;
        // [END data_model]
        // Show data_model
        System.out.println("\tBANG NHÂN VIÊN - CÔNG VIEC ");
        System.out.print("NV/CV\t");
        for (int i=0;i<numCV;i++){
            System.out.print("CV"+i+"\t");
        }
        System.out.println("");
        for (int i=0;i<numNV;i++){
            System.out.print(NV[i]+"\t");
            for (int j=i*numNV;j<i*numNV+numNV;j++){
                System.out.print(CV[j]+"\t");
            }
            System.out.println("");
        }
        // end Show data_model
        // Thuật toán giải quyết
        // Create the linear solver with the SCIP backend.
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
          System.out.println("Could not create solver SCIP");
          return;
        }
        // Variables
        // Tạo mảng 2 chiều có giá tị 0 hoặc 1, 1 nếu NV làm CV đó
        MPVariable[][] x = new MPVariable[numNV][numCV];
        for (int i = 0; i < numNV; ++i) {
          for (int j = 0; j < numCV; ++j) {
            x[i][j] = solver.makeIntVar(0, 1, "");
          }
        }
        // tạo Constraints : rằng buộc bài toán
        // Each worker is assigned to at most one task.
        for (int i = 0; i < numNV; ++i) {
          MPConstraint constraint = solver.makeConstraint(0, 1, ""); // 0 là ít nhất , 1 là nhiều nhất 
          for (int j = 0; j < numCV; ++j) {
            constraint.setCoefficient(x[i][j], 1);
          }
        }
        // Each task is assigned to exactly one worker.
        for (int j = 0; j < numCV; ++j) {
          MPConstraint constraint = solver.makeConstraint(1, 1, ""); // bắt buộc phải nhận giá trị là 1
          for (int i = 0; i < numNV; ++i) {
            constraint.setCoefficient(x[i][j], 1);
          }
        }
        // Objective _ đối tượng
        // gán mảng chi phí vào mảng đối tượng sau đó thực hiện tính toán 
        MPObjective objective = solver.objective();
        for (int i = 0; i < numNV; ++i) {
          int n=numCV;
          for (int j = 0; j < numCV; ++j) {
            objective.setCoefficient(x[i][j], CV[j+(i*n)]);
          }
        }
        objective.setMinimization(); // set tìm giá trị nhỏ nhất theo thuật toán
        MPSolver.ResultStatus resultStatus = solver.solve(); // kết quả dựa vào các giá trị và rằng buộc + đối tượng cost
        // Check that the problem has a feasible solution.
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL
            || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
                    System.out.println("Total cost: " + objective.value() + "\n");
                    for (int i = 0; i < numNV; ++i) {
                      int m=numCV;
                      for (int j = 0; j < numCV; ++j) {
                        if (x[i][j].solutionValue() > 0.5) {
                          System.out.println(
                              "Worker " + i + " assigned to task " + j + ".  Cost = " + CV[j+(i*m)]);
                        }
                      }
                    }
            } else 
            {
              System.err.println("No solution found.");
            }
    }
    private MIP(){}
}
