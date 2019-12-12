# phBill
			2019-6-9 ver. 1
This project initially implements an easy-to-operate personal accounting software, the current version 1.0.
The functions implemented in 1.0 are as follows:
* Login password
* Histogram analysis of income and expenditure in a month
* Database-based add / remove revenue and expenditure records
* Automatic positioning
* Bill attached date, description, (optional picture).

### background

**environment**

- OS：windows
- Tool: android studio
- AVD Nexus 5X API 29 x86
- JDK ≥ 1.8
- use androidx
- SDK ≥ 25，recommend 28

**components**

- Hellocharts library：Provide chart support 
- Andpermission：Provide permission management for mobile phones
- Smartfresh：Provide refreshed interface effects
- BRVAH：Provides pull-down and pull-down for more mechanisms
- FlexibleDivider：control the items in RecycleView
- Bomb：Provides cloud service related APIs for android phones

### Module design description

**activities**

  name             	description                                    
  sign up activity	Provide user / password input during login and error output in each case              
  login activity  	Provides warnings during registration, duplicate passwords, and blank usernames              
  main activity   	As the core activity of the app, as the main interface                 
  my fragment     	Inherit class Fragment, additional: automatically hide the function of useless modules, a set of variables about process interlocking
                  	                                        
                  	                                        
                  	                                        

main activity：


