export type SectionId = 'selecciones' | 'estadios' | 'categorias';
export type PackStatus = 'UNOPENED' | 'OPENED';
export type StickerType = 'CURRENT_PLAYER' | 'STADIUM' | 'LEGEND';

export interface Pack {
    id: string;
    size: number;
    status: PackStatus;
    openedAt?: string;
}

export interface StickerSlot {
    id: string;
    code: string;
    title: string;
    imageUrl?: string;
    owned: boolean;
    duplicates: number;
    sectionId: string;
    type?: StickerType;
    exchangeValue?: number; 
}

export interface AlbumPage {
    id: string;
    sectionId: string;
    pageNumber: number;
    title: string;
    slots: StickerSlot[];
}

export interface AlbumStatus {
    availablePacks: number;
    coins: number;
}

export interface Transaction {
    id: number;
    transactionType: string;
    amount: number;
    description: string;
    createdAt: string;
}