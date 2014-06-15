---           
layout: post
title: "Gradient descent for Logistic regression in octave"
date: 2014-02-01 14:56:19 UTC
comments: false
categories: machine-learning
---

I am a huge fan of machine learning classes on [coursera](http://www.ml-class.org). They do a great job of explaining machine learning techniques with lots of hands on . If you are following the course or watching videos you will find this post useful.       

In the discussion of Logistic Regression,  exercise two, we use fminunc function rather than standard gradient descent for minimizing for theta. Exercise does not discuss how to use gradient descent for the same. If you use the code of gradient descent of linear  regression exercise you don't get same values of theta . So you will be left wondering how to use gradient descent for logistic regression.  Even I also got stuck at same place and was able to figure it out after lot of trial and error. To save other's pain I am sharing following code for the same.


Create a file called gradientDescent.m and paste the following code

{% highlight octave %}

function [theta,cost] = gradientDescent(X, y, theta, alpha, num_iters)

%GRADIENTDESCENT Performs gradient descent to learn theta

%   theta = GRADIENTDESENT(X, y, theta, alpha, num_iters) updates theta by

%   taking num_iters gradient steps with learning rate alpha



% Initialize some useful values

m = length(y); % number of training examples

J_history = zeros(num_iters, 1);

theta_history = theta;



for iter = 1:num_iters

    h = sigmoid(X*theta);

    grad = (X'*(h - y))/m;

    theta = theta - alpha*grad;               

end

[cost,gradient] = costFunction(theta,X,y);       

{% endhighlight %}


Other than the calculation of h, the other code is identical to the gradient descent of the logisitic regression.

To call gradient descent , add the following lines to ex2.m

{% highlight octave %}

%%============= Part 4: Optimizing using Gradient Descent =============

alpha = 0.0014;

[theta,cost]= gradientDescent(X,y,
initial_theta,alpha,2000000);

% Print theta to screen

fprintf('Cost at theta found by gradient Descent: %f\n', cost);

fprintf('theta: \n');

fprintf(' %f \n', theta);



% Plot Boundary

plotDecisionBoundary(theta, X, y);



% Put some labels

hold on;

% Labels and Legend

xlabel('Exam 1 score')

ylabel('Exam 2 score')



% Specified in plot order

legend('Admitted', 'Not admitted')

hold off;



fprintf('\nProgram paused. Press enter to continue.\n');

pause;

{% endhighlight %}

Now you should have gradient descent working for logistic regression.



