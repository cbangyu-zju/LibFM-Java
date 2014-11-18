package kakao.test;
import kakao.data.Data;
import kakao.data.DataMetaInfo;
import kakao.algorithm.FM_Learn_Sgd;
import kakao.algorithm.FM_Model;
import kakao.algorithm.FM_Learn;
import kakao.algorithm.FM_Learn_Sgd_Element;
import kakao.util.Cmdline;

// import org.la4j.vector.dense.BasicVector;
import no.uib.cipr.matrix.DenseVector;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;


public class Test {
	public static void main(String[] args) throws IOException { try {
			long execTime = new Date().getTime();
			int argc = args.length;
			Cmdline cmdline = new Cmdline(argc, args);
			System.out.println("----------------------------------------------------------------------------");
			System.out.println("LibFM");
			System.out.println("  Version: 1.4.2");
			System.out.println("  Author: Kilho Kim, catharsistk@gmail.com");
			System.out.println("  Original Author: Steffen Rendle, srendle@libfm.org");
			System.out.println("  WWW:  http://imlab.snu.ac.kr");
			System.out.println("This program comes with ABSOLUTELY NO WARRANTY; for details see license.txt.");
			System.out.println("This is free software, and you are welcome to redistribute it under certain");
			System.out.println("conditions; for details see license.txt.");
			System.out.println("----------------------------------------------------------------------------");
			
			String param_task = cmdline.registerParameter("task", "r=regression, c=binary classification [MANDATORY]");
			String param_meta_file = cmdline.registerParameter("meta", "filename for meta information about data set");
			String param_train_file = cmdline.registerParameter("train", "filename for training data [MANDATORY]");
			String param_test_file = cmdline.registerParameter("test", "filename for test data [MANDATORY]");
			String param_val_file = cmdline.registerParameter("validation", "filename for validation data (only for SGDA)");
			String param_out = cmdline.registerParameter("out", "filename for output");

			String param_dim = cmdline.registerParameter("dim","'k0,k1,k2': k0=use bias, k1= use 1-way interactions, k2=dim of 2-way interactions; default=1,1,8");
			// FIXME: fixed regularization parameter input part
			String param_regular = cmdline.registerParameter("regular","'r0,r1,r2,r3' for SGD and ALS: r0=bias regularization, r1=1-way regularization, r2=2-way regularization, r3=user cluster regularization");
			// /end fixed
			String param_init_stdev = cmdline.registerParameter("init_stdev", "stdev for initialization of 2-way factors; default=0.1");
			String param_num_iter = cmdline.registerParameter("iter", "number of iterations; default=100");
			String param_learn_rate = cmdline.registerParameter("learn_rate", "learn_rate for SGD; default=0.1");
			
			String param_method = cmdline.registerParameter("method","learning method (SGD, SGDA, ALS, MCMC); default=MCMC");
			
			String param_verbosity = cmdline.registerParameter("verbosity","how much infos to print; default=0");
			String param_r_log = cmdline.registerParameter("rlog", "write measurements within iterations to a file; default=''");
			String param_help = cmdline.registerParameter("help","this screen");
			
			String param_relation = cmdline.registerParameter("relation", "BS: filenames for the relations, default=''");
			
			String param_cache_size = cmdline.registerParameter("cache_size", "cache size for data storage (only applicable if data is in binary format), default=infty");
			
			String param_do_sampling = "do_sampling";
			String param_do_multilevel = "do_multilevel";
			String param_num_eval_cases = "num_eval_cases";
			
			if (cmdline.hasParameter(param_help) || argc == 1) {
				cmdline.print_help();
			}
			cmdline.checkParameters();
			
			if (!cmdline.hasParameter(param_method)) {
				cmdline.setValue(param_method, "mcmc");
			}
			if (!cmdline.hasParameter(param_init_stdev)) {
				cmdline.setValue(param_init_stdev, "0.1");
			}
			if (!cmdline.hasParameter(param_dim)) {
				cmdline.setValue(param_dim, "1,1,8");
			}
			if (!cmdline.hasParameter(param_regular)) {
				cmdline.setValue(param_regular, "0");
			}
			
			if (cmdline.getValue(param_method).equals("als")) {
			// als is an mcmc without sampling and hyperparameter inference
				cmdline.setValue(param_method, "mcmc");
				if (!cmdline.hasParameter(param_do_sampling)) {
					cmdline.setValue(param_do_sampling, "0");
				}
				if (!cmdline.hasParameter(param_do_multilevel)) {
					cmdline.setValue(param_do_multilevel, "0");
				}
			}

                // (1) Load the data
                System.out.println("Loading train...");
                System.out.println(cmdline.getValue(param_train_file));
                Data trainData = 
                	new Data(cmdline.getValue(param_cache_size, 0),
            		!cmdline.getValue(param_method).equals("mcmc"),
            		// no original data for mcmc
            		!(cmdline.getValue(param_method).equals("sgd") || 
            		cmdline.getValue(param_method).equals("sgda")));	
                	// no transpose data for sgd, sgda
                trainData.load(cmdline.getValue(param_train_file));
                System.out.println("Loading test...");
                Data testData =
                	new Data(cmdline.getValue(param_cache_size, 0),
            		!cmdline.getValue(param_method).equals("mcmc"),
            		// no original data for mcmc
            		!(cmdline.getValue(param_method).equals("sgd") || 
            		cmdline.getValue(param_method).equals("sgda")));	
                	// no transpose data for sgd, sgda
                testData.load(cmdline.getValue(param_test_file));
                
                Data validationData = null;
                if (cmdline.hasParameter(param_val_file)) {
                	if (!cmdline.getValue(param_method).equals("sgda")) {
                		System.out.println("WARNING: Validation data is only used for SGDA. The data is ignored.");
                	} else {
                		System.out.println("Loading validation set...");
                		validationData =
                		new Data(cmdline.getValue(param_cache_size, 0),
                		!cmdline.getValue(param_method).equals("mcmc"),
                		// no original data for mcmc
                		!(cmdline.getValue(param_method).equals("sgd") || 
                		cmdline.getValue(param_method).equals("sgda")));	
                	// no transpose data for sgd, sgda
            		validationData.load(cmdline.getValue(param_val_file));
                	}
                }
                
                // TODO: (1.1.1) Load relational data
                // BasicVector relation;

                // (1.2) Load meta data
                System.out.println("Loading meta data...");
                // (main table)
                int numAllAttr = Math.max(trainData.numCols,testData.numCols);
                // if (validation != null) {}
                DataMetaInfo metaMain = new DataMetaInfo(numAllAttr);
                if (cmdline.hasParameter(param_meta_file)) {
                	metaMain.loadGroupsFromFile(cmdline.getValue(param_meta_file));
                }
                DataMetaInfo meta = metaMain;	// TODO: don't consider relation at this time
                
                // FIXME: (1.3) Register users from input data
                System.out.println("Register users from input data...");
                final int numUserAttrGroup = 1;  // FIXME: attribute group number for user id
                final int numClusterAttrGroup = 9;	// FIXME: attribute group number for user cluster
                int userAttrStartId = 1;
                int clusterAttrStartId = 0;
                for (int i = 1; i < numClusterAttrGroup; i++) {
                	clusterAttrStartId += meta.numAttrPerGroup.get(i);
                }  // getting the last featureId of the last attrGroup
                trainData.registerUsers(userAttrStartId, (int)meta.numAttrPerGroup.get(numUserAttrGroup), clusterAttrStartId+1, (int)meta.numAttrPerGroup.get(numClusterAttrGroup));
                // /end Register users from input data

                // TODO: build the joined meta table
                

                // (2) Setup the factorization machine
                System.out.println("Setting up the factorization machine...");
                FM_Model fm = new FM_Model();
                fm.numAttr = numAllAttr;
                fm.initStdev = cmdline.getValue(param_init_stdev,0.1);
                // set the number of dimensions in the factorization
                ArrayList<Integer> dim = cmdline.getIntValues(param_dim);
                // String[] dim = "1,1,8".split(",");
                fm.k0 = (dim.get(0) != 0);
                fm.k1 = (dim.get(1) != 0);
                fm.numFactor = dim.get(2);
                fm.init();
                

                // (3) Setup the learning method
                System.out.println("Setting up the learning method...");
                FM_Learn fml;
                //if (cmdline.getValue(param_method).equals("sgd")) {
                	fml = new FM_Learn_Sgd_Element();
                	((FM_Learn_Sgd)fml).numIter = cmdline.getValue(param_num_iter, 100);
                //} else if (cmdline.getValue(param_method).equals("sgda")) {
                	// fml = new FM_Learn_Sgd_Element_Adapt_Reg();
                	// ((FM_Learn_Sgd)fml).numIter = cmdline.getValue(param_num_iter, 100);
                	// ((FM_Learn_Sgd_Element_Adapt_Reg)fml).validation = validation;
                //} else if (cmdline.getValue(param_method).equals("mcmc")) {
                	// TODO: implement MCMC algorithm
                	// fml = new FM_Learn_Mcmc_Simultaneous();
                	// fml.validation = validation;
                	// fm.w_init_normal(fm.init_mean, fm.init_stdev);
				//} else {
					//System.out.println("unknown method");
                //}
                fml.fm = fm;
                fml.maxTarget = trainData.maxTarget;
                fml.minTarget = trainData.minTarget;
                fml.meta = meta;

                if (cmdline.getValue("task").equals("r")) {
                	fml.task = 0;
                } else if (cmdline.getValue("task").equals("c")) {
                	fml.task = 1;	
                	for (int i = 1; i < trainData.numRows; i++) {
                		if (trainData.target.get(i) <= 0.0) {
                                trainData.target.set(i, -1.0);
                        } else {
                                trainData.target.set(i, 1.0);
                        }
                	}
                	for (int i = 1; i < testData.numRows; i++) {
                        if (testData.target.get(i) <= 0.0) {
                                testData.target.set(i, -1.0);
                        } else {
                                testData.target.set(i, 1.0);
                        }
                	}
                	// if (validation != NULL)
                } else {
                	System.out.println("unknown task");
                }


                // (4) Init the logging
                fml.init(); 
                if (cmdline.getValue(param_method).equals("mcmc")) {
                } else {
                	// set the regularization; for standard SGD, groups are not supported
                	ArrayList<Double> reg = cmdline.getDblValues(param_regular);
                	// FIXME: fixed regularization parameter input part
                	if (reg.size() == 0) {
                		fm.reg0 = 0.0;
                		fm.regw = 0.0;
                		fm.regv = 0.0;
                		fm.regu = 0.0;
                	} else if (reg.size() == 1) {
                		fm.reg0 = reg.get(0);
                		fm.regw = reg.get(0);
                		fm.regv = reg.get(0);
                		fm.regu = reg.get(0);
                	} else {
                		fm.reg0 = reg.get(0);
                		fm.regw = reg.get(1);
                		fm.regv = reg.get(2);
                		fm.regu = reg.get(3);	// this one is effective, actually
                	}
                	// /end of fixed
                }
                	
                // set the learning rates (individual per layer)
                FM_Learn_Sgd fmlsgd = (FM_Learn_Sgd)fml;
                ArrayList<Double> lr = cmdline.getDblValues(param_learn_rate);
                if (lr.size() == 1) {
                	fmlsgd.learnRate = lr.get(0);
                	fmlsgd.learnRates = new DenseVector(3);
                	for (int i = 0; i < fmlsgd.learnRates.size(); i++) {
                		fmlsgd.learnRates.set(i,lr.get(0));
                	}
                } else if (lr.size() == 3) {
                	fmlsgd.learnRate = 0;
                	fmlsgd.learnRates = new DenseVector(3);
                	fmlsgd.learnRates.set(0,lr.get(0));
                	fmlsgd.learnRates.set(1,lr.get(1));
                	fmlsgd.learnRates.set(2,lr.get(2));
                } else {
                	throw new Exception("learning rates error: its size must be 1 or 3");
                }
                	
                // () learn
                fml.learn(trainData, testData);
                
                // () Prediction at the end (not for mcmc and als)
                if (!cmdline.getValue(param_method).equals("mcmc")) {
                	System.out.println("Final\t Train=" + fml.evaluate(trainData) + "\tTest=" + fml.evaluate(testData));
                }
                
                // () Save prediction
                if (cmdline.hasParameter(param_out)) {
                	DenseVector pred;
                	pred = new DenseVector(testData.numRows+1);
                	fml.predict(testData, pred);
                	
                	BufferedWriter fData = new BufferedWriter(new FileWriter(cmdline.getValue(param_out)));
                	for (int i = 1; i < pred.size(); i++) {
                		fData.write(Double.toString(pred.get(i)));
                		fData.newLine();
                	}
                	fData.close();
                }
                
			execTime = new Date().getTime() - execTime;		// time difference while executing (in ms)
			System.out.println("execTime(ms) = " + execTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
