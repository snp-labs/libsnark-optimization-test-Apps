#include <stdarg.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>

void hello_devworld(void);

/**
 * Add two signed integers.
 *
 * On a 64-bit system, arguments are 32 bit and return type is 64 bit.
 */
long long add_numbers(int x, int y);

/**
 * Take a zero-terminated C string and return its length as a
 * machine-size integer.
 */
unsigned long string_length(const char *sz_msg);

unsigned long leven(const char *s1, const char *s2);

char *give_me_letter_a(unsigned long count);

void countdown(void (*callback)(int));

char *do_encryption(void);
