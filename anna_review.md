Too many parts are missing or incomplete.
 I hope that the below comments will help guide you towards a complete report! 
- Correctness 
  - In the Introduction, you say that parsing is part of compilers and interpreters, but what does parsing actually mean in this context? (You'd need to add something about programming languages being context-free and thus expressible by a context-free grammar, and therefore we can check that the syntax of a program is correct using parsing for context-free languages.) 
  - Going from talking about parsing to defining CFGs is confusing to me as a reader, how do these connect and why do we need a definition of CFGs? 
- In Section 1.
1, the =>* symbol is not defined (nor is *).
 You also mention generation, but how does the generation work? 
- The complexity of the naive algorithm has to be in place.
 Imagine that you pick the two "longest" subtrees in the worst-case recursion tree, i.
e.
, the subtrees rooted in the instances [1][n-1] and [2][n], and disregard the rest of the tree (which gives us a lower bound on the worst-case).
 Then, for each level, we again pick the two "longest" cases, so we get a branching factor of 2.
 [Fill in the rest yourself, and email me in case of questions.
] 
- For the other time complexities, remember that the grammar is not part of the input, so you should clarify that |G| is a constant in our case.
 Also, for the bottom-up version, I need a reasoning to buy that it is \Theta(n^3).
 
- The algorithm presentations are very good.
 The only comment I have here is that I think that doing the alpha[i][j] thing implies that we have to make copies of the string.
 Argumentation and analysis 
- The analysis is rather thin.
 What results are you expecting for the various grammars? What results do you get? Do the results align with the theoretical expectations? What are the cases for which td and bu differ? 
- Where are the conclusions? Structure, language, and writing style 
- The English grammar is a bit shaky at times.
 
- The format of your report seems to vary: sometimes a new paragraph is marked using a newline and an indentation unless the paragraph is the first one under a title, sometimes only a newline is used (this second way is discouraged).
 
- Putting all of the figures in the appendix makes it more difficult to follow the line of reasoning.
 There is a way to make the figures span over the two columns, I think it's the figure* environment or something similar.
 It is hard to understand to what figures you are referring to in the text.
 
- The plots are not super clear, and in for example the one under Appendix 0.
9, there is no way to know what the top-down algorithm's performance looks like.
 In this case, you could multiply the blue line with a constant between 0 and 1.
 
- The y axis does not give us complexity, but running time or number of operations, I guess
