#include "fraction.h"
#include <ios>
Fraction::Fraction(int numerator, int denominator)
    :Numerator(numerator), Denominator(denominator)
{
    //Throw exepction
    if(Denominator == 0)
        throw std::range_error("Devided by zero.");
    if(Numerator==0) Denominator=1;
    Reducte();
}

Fraction Fraction::operator+(const int &num) const
{
    return *this + Fraction(num);
}

Fraction Fraction::operator+(const Fraction &fraction) const
{
    return Fraction(Numerator * fraction.Denominator + Denominator * fraction.Numerator,
                    fraction.Denominator * Denominator);
}

Fraction Fraction::operator-(const int &num) const
{
    return *this - Fraction(num);
}

Fraction Fraction::operator-(const Fraction &fraction) const
{
    return Fraction(Numerator * fraction.Numerator - Denominator * fraction.Numerator,
                    fraction.Denominator * Denominator);
}

Fraction Fraction::operator-() const
{
    return Fraction(-Numerator,Denominator);
}

Fraction Fraction::operator*(const int &num) const
{
    return Fraction(Numerator * num, Denominator);
}

Fraction Fraction::operator*(const Fraction &fraction) const
{
    return Fraction(Numerator * fraction.Numerator, Denominator * fraction.Denominator);
}

Fraction Fraction::operator/(const int &num) const
{
    return Fraction(Numerator, Denominator * num);
}

Fraction Fraction::operator/(const Fraction &fraction) const
{
    return *this * ~fraction;
}

Fraction Fraction::operator~() const
{
    return Fraction(Denominator, Numerator);
}

bool Fraction::operator==(const Fraction &fraction) const
{
    return fraction.Numerator == Numerator &&
            fraction.Denominator == Denominator;
}

bool Fraction::operator==(const int &num) const
{
    return Denominator==1 && num == Numerator;
}

bool Fraction::operator!=(const Fraction &fraction) const
{
    return !(*this == fraction);
}

bool Fraction::operator!=(const int &num) const
{
    return !(*this == num);
}

bool Fraction::operator<(const Fraction &fraction) const
{
    return Numerator * fraction.Numerator < Denominator * fraction.Numerator;
}

bool Fraction::operator<(const int &num) const
{
    return Numerator < Denominator * num;
}

bool Fraction::operator>(const Fraction &fraction) const
{
    return Numerator * fraction.Numerator > Denominator * fraction.Numerator;
}

bool Fraction::operator>(const int &num) const
{
    return Numerator > Denominator * num;
}


bool Fraction::operator<=(const Fraction &fraction) const
{
    return *this < fraction || *this == fraction;
}

bool Fraction::operator<=(const int &num) const
{
    return *this < num || *this == num;
}

bool Fraction::operator>=(const Fraction &fraction) const
{
    return *this > fraction || *this == fraction;
}

bool Fraction::operator>=(const int &num) const
{
    return *this > num || *this == num;
}

Fraction::operator bool() const
{
    return Numerator!=0;
}

Fraction::operator double() const
{
    return (double)Numerator/Denominator;
}

std::ostream &operator<<(std::ostream &out,const Fraction &fraction)
{
    out<<fraction.Numerator;
    if(fraction.Denominator != 1)
        out<<'/'<<fraction.Denominator;
    return out;
}

std::istream &operator>>(std::istream &in,Fraction &fraction)
{
    //Get like a/b;
    char c;
    int numerator, denominator;
    in>>numerator>>c>>denominator;
    if(c != '/')
    {
        in.clear(std::ios_base::failbit);
        throw std::invalid_argument("Invalid input");
    }
    else
    {
        fraction.Numerator = numerator;
        fraction.Denominator = denominator;
        fraction.Reducte();
    }
    return in;
}

void Fraction::Reducte()
{
    if(Denominator < 0)
    {
        //Put the sign to numerator
        Numerator = -Numerator;
        Denominator = -Denominator;
    }
    //get sign
    bool sign = Numerator < 0 ? true : false;
    if(sign) Numerator = -Numerator;
    while(((Numerator|Denominator)&1) == 0)
        Numerator>>=1, Denominator>>=1;
    int gcd = Gcd(Numerator, Denominator);
    if(gcd > 1)
        Numerator/=gcd, Denominator/=gcd;
    if(sign) Numerator = -Numerator;
}

//Binary gcd algorithm
int Fraction::Gcd(int u, int v) const
{
    if (u == 0) return v;
    if (v == 0) return u;

    while ((u & 1) == 0)
        u >>= 1;
    int t;
    do
    {
        while ((v & 1) == 0)
        v >>= 1;
        if (u > v)
        {
            t = v;
            v = u;
            u = t;
        }
        v = v - u;
    } while (v != 0);
    return u;
}


