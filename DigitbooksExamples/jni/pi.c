/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

int *resul;	/* TROIS TABLEAUX DYNAMIQUES CONTENANT RESPECTIVEMENT */
int *resul1;	/* LE RESULTAT FINAL ET LES RESULTATS INTERMEDIAIRES   */
int *resul2;
int lim;	/* NOMBRE DE DECIMALES DEMANDEES */
int K;		/* POUR GERER LES CASES NULLES */
int MAX;	/* DIMENSION DES TABLEAUX */
int BASE;	/* BASE DE NUMERATION CORRESPOND AU GROUPEMENT DES CHIFFRES */
int GBASE;	/* NOMBRE DE GROUPEMENT DES CHIFFRES POUR LA SORTIE */
int I, J;	/* NOMBRE D'ITERATIONS POUR CHAQUE ARCTG */

#include <android/log.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <jni.h>

#define SIZE_T          (size_t)

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */
jintArray
Java_fr_digitbooks_android_examples_chapitre10_PiCalculatingNdkTask_calculatePi( JNIEnv* env,
                                                  jobject thiz, jint decimalNumber )
{
	jintArray result;
    int k, i, NB;
	lim = decimalNumber;


	if(lim <= 1500) {
		BASE=1000000;
		NB=6;
		GBASE=10;
	}
	else {
		if(lim > 15000) {
			__android_log_write(ANDROID_LOG_ERROR, "Digitbooks_Ndk", "Pas plus de 15000 decimales merci" );
			exit(0);
		}
		BASE=100000;
		NB=5;
		GBASE=12;
	}
	MAX=3+lim/NB;
	resul=(int *)malloc(4*MAX);
	resul1=(int *)malloc(4*MAX);
	resul2=(int *)malloc(4*MAX);
	result = (*env)->NewIntArray(env, MAX);
	memset((char *)resul1,0,SIZE_T 4*MAX);
	memset((char *)resul2,0,SIZE_T 4*MAX);
	memset((char *)resul,0,SIZE_T 4*MAX);
	I=arctg_5(resul1,5);
	memcpy((char *)resul2,(char *)resul,SIZE_T 4*MAX);
	memset((char *)resul,0,SIZE_T 4*MAX);
	J=arctg_239(resul1,239);
	//display("resul", resul);
	K=0;
	sous(resul2,resul);
	__android_log_print(ANDROID_LOG_ERROR, "Digitbooks_Ndk", "Decimales affichees: %d  nombre d'iterations: %d , %d (%d)", NB*(MAX-2), I, J, I+J );
	
	//char result[lim * 7 / 6 + 10];
	//todec(resul2, result);
	(*env)->SetIntArrayRegion(env, result, 0, MAX, resul2);
	return result;
}

todec(int tab[], char* result)	/* CONVERSION EN DECIMAL */

{
	char buffer[10];
	register i;
	int a;
	int BS10 = BASE/10;
	sprintf (result, "%d.", tab[0]);
	//__android_log_print(ANDROID_LOG_ERROR, "Digitbooks_Ndk", "pi = %s", buffer );
	for(i=1;i<MAX-1;++i) {
		a=tab[i];
		a = a == 0 ? 1 : a;
		while(a < BS10) {
			strcat (result,"0");
			a=10*a;
		}
		int n = sprintf (buffer, "%d%s", tab[i],i%GBASE ? " " : " ");
		strncat (result, buffer, n);
		//__android_log_print(ANDROID_LOG_ERROR, "Digitbooks_Ndk", "%d%s", tab[i],i%GBASE ? " " : "\n       " );
	}
	//__android_log_print(ANDROID_LOG_ERROR, "Digitbooks_Ndk", "pi = %s", result );
}

add(a,b)	/* ADDITION DE DEUX TABLEAUX DE NOMBRES */
int a[], b[];

{
	register i, r=0;		/* r EST LA RETENUE */
	for(i=MAX-1;i >= K;--i) {
		r=a[i]+r+b[i];
		if(r >= BASE) {
			a[i]=r-BASE;
			r=1;
		}
		else {
			a[i]=r;
			r=0;
		}
	}
	while(r) {	/* PROPAGATION DE LA RETENUE */
		r=a[i]+r;
		if(r >= BASE) {
			a[i]=r-BASE;
			r=1;
		}
		else {
			a[i]=r;
			r=0;
		}
		--i;
	}
}

sous(a,b)	/* SOUSTRACTION DE DEUX TABLEAUX DE NOMBRES. LE SIGNE DU */
int a[], b[];	/* RESULTAT EST SUPPOSE POSITIF				 */

{
	register i, r=0;
	for(i=MAX-1;i >= K;--i) {
		r=a[i]-r-b[i];
		if(r < 0) {
			a[i]=r+BASE;
			r=1;
		}
		else {
			a[i]=r;
			r=0;
		}
	}
	while(r) {	/* PROPAGATION DE LA RETENUE */
		r=a[i]-r;
		if(r < 0) {
			a[i]=r+BASE;
			r=1;
		}
		else {
			a[i]=r;
			r=0;
		}
		--i;
	}
}

mul(tab,n)     /* MULTIPLICATION D'UN TABLEAU DE NOMBRES PAR UN ENTIER POSITIF*/
int tab[], n;

{
	register i, S=0;
	for(i=MAX-1;i>=K;--i) {
		tab[i]=tab[i]*n+S;
		S=tab[i]/BASE;
		tab[i] = tab[i]%BASE;
	}
	tab[i]=tab[i]*n+S;
	while((S=tab[i])>=BASE) {
		tab[i-1] = tab[i-1] + S/BASE;
		tab[i] = S%BASE;
		--i; --K;
	}
	--K;
	while(tab[K] == 0) ++K;
}

divisi(t,n)	/* DIVISION D'UN TABLEAU DE NOMBRES PAR UN ENTIER */
int t[], n;

{
	int a, i;
	//printf("K = %d\n",K);
	a=t[K];
	//printf("Max = %d\n",MAX);
	for(i=K;i<MAX;++i) {
		//printf("a/n = %d/%d\n",a,n);
		t[i]=a/n;
		//printf("i = %d\n",i);
		a=BASE*(a%n)+t[i+1];
		//printf("a = %d\n",a);
	}
	while(t[K] == 0) ++K;	/* GESTION DES CASES NULLES */
}

display(char c[],int array[]) /* print array */

{
	int i;
	printf("%s", c);
	printf(" = {");
	for (i = 0; i < MAX; i++) {
		//printf("%p:%d",&array[i],array[i]);
		printf("%d",array[i]);
		if (i < MAX - 1) {
			printf(", ");
		}
	}
	printf("}\n");
}

arctg_239(tab,p)	/* 4*arctg(1/p) POUR p DE L'ORDRE DE GRANDEUR DE 239 */
int tab[], p;

{
	register i;
	K=0;
	tab[0]=4;
	divisi(tab,p);
	add(resul,tab);
	for(i=0;K < MAX;++i) {
		divisi(tab,(2*i+3));
		divisi(tab,p);/*ON FAIT 2 DIVISIONS SUCCESSIVES AU LIEU D'UNE*/
		divisi(tab,p);/*POUR EVITER DE DEPASSER LES 31 BITS          */
		mul(tab,(2*i + 1));
		(i+1)%2 ? sous(resul,tab) : add(resul,tab);
	}
	return(i);
}

arctg_5(tab,p)	/* 16*arctg(1/p) POUR p DE L'ORDRE DE GRANDEUR DE 5 */
int tab[], p;

{
	register i;
	K=0;
	tab[0]=16;
	//display("tab", tab);
	//display("resul", resul);
	divisi(tab,p);
	//display("tab", tab);
	add(resul,tab);
	//display("tab", tab);
	//display("resul", resul);
	p=p*p;
	for(i=0;K < MAX;++i) {
		divisi(tab,(2*i+3));
		//display("tab", tab);
		divisi(tab,p);
		//display("tab", tab);
		mul(tab,(2*i + 1));
		//display("tab", tab);
		(i+1)%2 ? sous(resul,tab) : add(resul,tab);
		//display("tab", tab);
		//display("resul", resul);
	}
	return(i);
}