#include "hlist.h"
#include <stdlib.h>
#include <string.h>

//Allocate a node and initialize it.
static HList *newNode()
{
    HList *node = (HList*)malloc(sizeof(HList));
    node->Element = NULL;
    node->next = NULL;
    node->ElementType.id = 0;
    node->ElementType.size = 0;
    return node;
}

//Find the next node and return the previous node with type and element.
static void seek(HList **node, HList **pre, void *element, Type type)
{
    if(pre)
        *pre = *node;
    HList *current = (*node)->next;
    while(current)
    {
        if(current->ElementType.id == type.id)
            if(!memcmp(current->Element,element,type.size))
                break;
        if(pre)
            *pre = current;
        current = current->next;
    }
    *node = current;
}

//Return the last element and its previous element.
static HList *last(HList *list, HList **pre)
{
    while(list->next)
    {
        if(pre)
            *pre = list;
        list = list->next;
    }
    return list;
}

//New an element copying the provided element.
static void *newElement(void *element, Type type)
{
    void *e = malloc(type.size);
    memcpy(e,element,type.size);
    return e;
}

//Delete an element and reset its pointer.
static void deleteElement(HList **node)
{
    free((*node)->Element);
    free(*node);
    *node = NULL;
}

void Append(HList *list, void *element, Type type)
{
    if(!list) return;
    list = last(list,NULL);
    list->next = newNode();
    list = list->next;
    list->Element = newElement(element,type);
    list->ElementType = type;
}

void Remove(HList *list, void *element, Type type)
{
    if(!list || !list->next) return;
    list = list->next;
    HList *pre;
    seek(&list,&pre,element,type);
    if(!list) return;
    pre->next = list->next;
    deleteElement(&list);;
}


HList *Find(HList *list, void *element, Type type)
{
    seek(&list,NULL,element,type);
    return list;
}

HList *Create()
{
    return newNode();
}

HList *TypeNext(HList *list, Type type)
{
    if(!list) return NULL;
    list = list->next;
    while(list && list->ElementType.id != type.id)
        list = list->next;
    return list;
}

void PrintAll(HList *list)
{
    if(!list)
        return;
    list = list->next;
    while(list && list->ElementType.print)
    {
        list->ElementType.print(list->Element);
        list = list->next;
    }
}

void Push(HList *list, void *element, Type type)
{
    if(!list)
        return;
    HList *New = newNode();
    New->next = list->next;
    list->next = New;
    New->Element = newElement(element,type);
    New->ElementType = type;
}

void Reverse(HList *list)
{
    HList *cur, *next, *last;
    cur = list->next;
    last = NULL;
    while(cur)
    {
        next = cur->next;
        cur->next = last;
        last = cur;
        cur = next;
    }
    list->next = last;
}

void Merge(HList *list, HList *operand)
{
    if(!list||!operand) return;
    while(list->next)
        list = list->next;
    list->next = operand->next;
    operand->next = NULL;
}

void Empty(HList *list)
{
    if(!list)
        return;
    HList *next;
    next = list->next;
    list->next = NULL;
    list = next;
    while(list)
    {
        next = list->next;
        deleteElement(&list);
        list = next;
    }
}

void Pop(HList *list)
{
    HList *next = list->next;
    list->next = next->next;
    deleteElement(&next);
}

void Out(HList *list)
{
    HList *pre;
    list = last(list,&pre);
    pre->next = NULL;
    deleteElement(&list);
}

int IsEmpty(HList *list)
{
    //Skip dummy head.
    if(list->next)
        return 0;
    else
        return 1;
}
