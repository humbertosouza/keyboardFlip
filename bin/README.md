# Keyboard Rotator 0.7

By Humberto Ribeiro de Souza

## How to use

Download ZIP file contained. The source file is available under the src folder.

On a terminal, using a JRE 6 engine or newer, run

```shell 
$java -jar keyboardv1.jar** 
```

Without parameters, the program runs by default:  
- Lists the keyboard sets for [H,V, 5,-12,15,50,-50,10,1000,-1000]
- And the converted "Hello World" string as specified for the resulting keyboard of the combination H, V, H, 5, V, -12 

## Converting files to the new keyboard set

On a terminal, using a JRE 6 engine or newer, run
 
```shell
$java -jar keyboardv1.jar transforms.txt sourceBigText.txt 
```
#### Inputs

The transforms.txt file should be a CSV file containing the desired moves;
The sourceBigText.txt is the text to be converted. 
 
#### Outputs

The program will print the resulting text, without transforming characters that is not part of the existing map.
